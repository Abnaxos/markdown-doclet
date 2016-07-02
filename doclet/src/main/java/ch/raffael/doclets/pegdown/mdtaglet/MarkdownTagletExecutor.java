/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
 *
 * This file is part of pegdown-doclet.
 *
 * pegdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pegdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pegdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package ch.raffael.doclets.pegdown.mdtaglet;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.raffael.doclets.pegdown.mdtaglet.MarkdownTagletUtils.stripBlanksFromLineEnd;

/**
 * MarkdownTagletExecutor is responsible for extracting inline tags for each {@linkplain #register(MarkdownTaglet) registered}
 * {@link MarkdownTaglet}, and applying the {@code tags arguments} to the tag implementation.
 */
public final class MarkdownTagletExecutor {

    private static final String STR_LEADWS_REGEX = "(?<leadws>\\s*)(?<tagex>";
    private static final String STR_TRAILWS_REGEX = ")(?<trailws>\\s*)";

    private static final List<String> STR_TAGS_REGEX_LIST = Arrays.asList(
            "\\{\\{(?<tag0>",           // {{<tag0> }}
            "\\{%(?<tag1>",             // {%<tag1> %}
            "\\{\\$(?<tag2>"            // {$<tag2> $}
    );

    private static final String STR_TAG_ARG_REGEX_SEPARATOR = ")\\p{Blank}*";

    private static final List<String> STR_ARGS_REGEX_LIST = Arrays.asList(
            "(?<args0>[^}]*)\\}\\}",       // {{<tag0> <args0>}}
            "(?<args1>[^%]*)%\\}",         // {%<tag1> <args1>%}
            "(?<args2>[^%]*)\\$\\}"        // {$<tag2> <args2>$}
    );


    private static List<String> tagGroups = Arrays.asList("tag0", "tag1", "tag2");
    private static List<String> argsGroups = Arrays.asList("args0", "args1", "args2");

    private static final Pattern TAG_ARGS_PATTERN = Pattern.compile("(?<arg>\"[^\"]+\"|'[^']+'|\\S+)");

    private final Map<String, MarkdownTaglet> tags = new HashMap<>();

    private Pattern tagletPattern = null;
    private MarkdownTagletErrorHandler errorHandler;

    /**
     * Set the error handler.
     *
     * @param errorHandler the error handler
     * @return self
     */
    public MarkdownTagletExecutor setErrorHandler(MarkdownTagletErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Register an {@link MarkdownTaglet}.
     * <p>
     * **Caution**: If already an InlineTag with the same {@linkplain MarkdownTaglet#getName() name} exists, then
     * <p>
     * + {@link MarkdownTagletErrorHandler#overrideMarkdownTaglet(MarkdownTaglet, MarkdownTaglet)} will be called
     * + the existing will be replaced by the new one.
     *
     * @param markdownTaglet the {@code InlineTag}
     * @return self
     */
    public MarkdownTagletExecutor register(MarkdownTaglet markdownTaglet) {
        final MarkdownTaglet old = tags.put(markdownTaglet.getName(), markdownTaglet);
        if (old != null) {
            errorHandler.overrideMarkdownTaglet(old, markdownTaglet);
        }

        return this;
    }

    /**
     * Parse the markup and applies all {@linkplain #register(MarkdownTaglet) registered} and found {@link MarkdownTaglet}s.
     *
     * @param markup the (raw) markup
     * @return the generated markdown
     */
    public String apply(final String markup) {
        if (tags.isEmpty()) {
            return markup;
        }

        if (null == tagletPattern) {
            tagletPattern = Pattern.compile(createTagletPattern(), Pattern.MULTILINE);
        }

        final Matcher matcher = tagletPattern.matcher(markup);
        if (!matcher.find()) {
            return markup;
        }

        matcher.reset();
        return doApply(matcher);
    }

    private String doApply(Matcher tagMatcher) {
        final StringBuffer result = new StringBuffer();
        while (tagMatcher.find()) {
            final String tagExpr = tagMatcher.group("tagex");

            // Resolve the leading/trailing whitespaces
            final String leadingWhiteSpaces = tagMatcher.group("leadws");
            final String trailingWhiteSpaces = tagMatcher.group("trailws");

            // Resolve taglet, the arguments and the arguments as list
            final MarkdownTaglet taglet = resolveTaglet(tagMatcher);
            final String arguments = resolveArguments(tagMatcher);

            String markdown;
            if (taglet.useArgumentValidator()) {
                markdown = renderTaglet(taglet, arguments, tagExpr, leadingWhiteSpaces, trailingWhiteSpaces);
            } else {
                markdown = renderRawTaglet(taglet, arguments, tagExpr, leadingWhiteSpaces, trailingWhiteSpaces);

            }
            tagMatcher.appendReplacement(result, markdown);
        }
        tagMatcher.appendTail(result);
        return result.toString();
    }

    private String renderTaglet(MarkdownTaglet taglet, String arguments, String tagExpr, String leadingWhiteSpaces, String trailingWhiteSpaces) {
        String markdown;

        final List<String> argumentList = toArgumentList(arguments);

        // validate arguments
        final ArgumentValidator argumentValidator = taglet.getArgumentValidator();
        final ValidationResult validationResult = argumentValidator.validate(argumentList);

        if (validationResult.isValid()) {
            try {
                // do the rendering
                markdown = doApplyWhiteSpacePreserver(
                        leadingWhiteSpaces,
                        trailingWhiteSpaces,
                        taglet.getWhiteSpacePreserver(),
                        renderTag(taglet, argumentList)
                );

            } catch (Exception ex) {
                markdown = renderUnexpectedException(leadingWhiteSpaces, tagExpr, trailingWhiteSpaces, ex);
                errorHandler.caughtUnexpectedException(
                        taglet,
                        tagExpr,
                        ex
                );
            }
        } else {
            markdown = renderInvalidResult(leadingWhiteSpaces, tagExpr, trailingWhiteSpaces, validationResult);
            errorHandler.invalidTagletArguments(taglet, tagExpr + " << " + validationResult.getError());
        }

        return markdown;
    }

    private String renderRawTaglet(MarkdownTaglet taglet, String arguments, String tagExpr, String leadingWhiteSpaces, String trailingWhiteSpaces) {
        String markdown;

        try {
            // do the rendering
            markdown = doApplyWhiteSpacePreserver(
                    leadingWhiteSpaces,
                    trailingWhiteSpaces,
                    taglet.getWhiteSpacePreserver(),
                    renderRawTag(taglet, arguments)
            );

        } catch (Exception ex) {
            markdown = renderUnexpectedException(leadingWhiteSpaces, tagExpr, trailingWhiteSpaces, ex);
            errorHandler.caughtUnexpectedException(
                    taglet,
                    tagExpr,
                    ex
            );
        }
        return markdown;
    }

    private String renderUnexpectedException(String leadingWhiteSpaces, String tagExpr, String trailingWhiteSpaces, Exception ex) {
        return renderError(leadingWhiteSpaces, tagExpr, trailingWhiteSpaces, ex.getClass().getName() + ": " + ex.getMessage());
    }

    private String renderInvalidResult(String leadingWhiteSpaces, String tagExpr, String trailingWhiteSpaces, ValidationResult validationResult) {
        return renderError(leadingWhiteSpaces, tagExpr, trailingWhiteSpaces, validationResult.getError());
    }

    private static String renderError(String leadingWhiteSpaces, String tagExpr, String trailingWhiteSpaces, String message) {
        return leadingWhiteSpaces + tagExpr + " << " + message + trailingWhiteSpaces;
    }

    private String resolveArguments(Matcher tagMatcher) {
        for (String argsGroup : argsGroups) {
            final String args = tagMatcher.group(argsGroup);
            if (args != null) {
                return args;
            }
        }
        return "";
    }

    private MarkdownTaglet resolveTaglet(Matcher tagMatcher) {
        String tagName = "unknown";
        for (String tagGroup : tagGroups) {
            tagName = tagMatcher.group(tagGroup);
            if (tagName != null)
                break;
        }
        return tags.get(tagName).createNewInstance();
    }

    private String doApplyWhiteSpacePreserver(String leadingWhiteSpaces, String trailingWhiteSpaces, WhiteSpacePreserver whiteSpacePreserver, String markdown) {
        return whiteSpacePreserver.leading(leadingWhiteSpaces) + markdown + whiteSpacePreserver.trailing(trailingWhiteSpaces);
    }


    private String renderTag(MarkdownTaglet tag, List<String> arguments) throws Exception {
        return stripBlanksFromLineEnd(tag.render(arguments));
    }

    private String renderRawTag(MarkdownTaglet tag, String arguments) throws Exception {
        return tag.renderRaw(arguments);
    }

    private static List<String> toArgumentList(String arguments) {
        final List<String> argList = new ArrayList<>();
        final Matcher matcher = TAG_ARGS_PATTERN.matcher(arguments);
        while (matcher.find()) {
            argList.add(strip(matcher.group("arg")));
        }
        return argList;
    }

    private static String strip(String arg) {
        return StringUtils.strip(arg, "\"'");
    }

    private String createTagletPattern() {
        return STR_LEADWS_REGEX
                + createTagletPattern(StringUtils.join(tags.keySet(), "|"))
                + STR_TRAILWS_REGEX;
    }

    /**
     * Creates the taglet pattern from `tagNames`.
     * @param tagNames the tag names
     * @return the regex for the known taglets.
     */
    private static String createTagletPattern(String tagNames) {
        final StringBuilder regex = new StringBuilder();
        final int size = STR_TAGS_REGEX_LIST.size();
        for (int regexIdx = 0; regexIdx < size; regexIdx++) {
            final String tagRegex = STR_TAGS_REGEX_LIST.get(regexIdx);
            final String argsRex = STR_ARGS_REGEX_LIST.get(regexIdx);
            if (regexIdx > 0) {
                regex.append('|');
            }
            regex.append(tagRegex).append(tagNames).append(STR_TAG_ARG_REGEX_SEPARATOR).append(argsRex);

        }
        return regex.toString();
    }


}
