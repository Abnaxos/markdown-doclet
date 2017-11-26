package ch.raffael.mddoclet.core.ast;


import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.raffael.mddoclet.core.ast.DocNode.Type;
import ch.raffael.nullity.Nullable;


/**
 * @author Raffael Herzog
 */
final class Lexer {

    // G_*: constants for group names
    // F_*: regexp fragments

    private static final String G_TAGSTART = "tagstart";
    private static final String G_TAGEND = "tagend";
    private static final String G_TAGNAME = "tagname";

    private static final String F_WS = "([\\s&&[^\\n\\r]])";
    private static final Pattern COMMENT_START = Pattern.compile("\\s*/\\*\\*");
    private static final Pattern NEWLINE = Pattern.compile("\\n|\\r\\n?");
    private static final Pattern WHITESPACE = Pattern.compile(F_WS + "+");
    private static final Pattern LEAD = Pattern.compile(F_WS + "*\\*" + F_WS + "?");
    private static final Pattern TEXT = Pattern.compile("\\S+");

    private static final String F_TAG_NAME = "(?<" + G_TAGNAME + ">[\\S&&[^{}]]+)";
    private static final String F_INLINE_TAG_START = "(?<" + G_TAGSTART + ">\\{[{@])" + F_TAG_NAME;
    private static final Pattern INLINE_TAG_START = Pattern.compile(F_INLINE_TAG_START);
    private static final Pattern INLINE_TAG_START_OR_END_JAVADOC = Pattern.compile("(" + F_INLINE_TAG_START + ")|(?<" + G_TAGEND + ">})");
    private static final Pattern INLINE_TAG_START_OR_END_WIKI = Pattern.compile("(" + F_INLINE_TAG_START + ")|(?<" + G_TAGEND + ">}})");
    private static final Pattern BLOCK_TAG_START = Pattern.compile("(?<lead>" + F_WS + "*)(?<" + G_TAGSTART + ">@)" + F_TAG_NAME);

    private final String input;
    private final Matcher matcher;
    private final Matcher tagMatcher;

    private boolean startOfComment;
    private boolean startOfLine;
    private boolean postLead;

    @Nullable
    private DocNode commentEndDocNode = null;
    private final Deque<Pattern> inlineTagPatternStack = new ArrayDeque<>();

    private Deque<DocNode> nodeQueue = new ArrayDeque<>(4);

    Lexer(String input, boolean includesCommentDelimiters) {
        this.input = input;
        matcher = LEAD.matcher(input);
        tagMatcher = INLINE_TAG_START.matcher(input);
        startOfLine = true;
        postLead = false;
        startOfComment = includesCommentDelimiters;
    }

    List<DocNode> toList() {
        ArrayList<DocNode> nodes = new ArrayList<>();
        for(DocNode node; (node=next())!=null;) {
            nodes.add(node);
        }
        return nodes;
    }

    @Nullable
    DocNode next() {
        DocNode next = findNext();
        startOfComment = false;
        startOfLine = typeOf(next) == Type.NEWLINE;
        postLead = typeOf(next) == Type.LEAD;
        return next;
    }

    @Nullable
    private DocNode findNext() {
        if (!nodeQueue.isEmpty()) {
            return nodeQueue.poll();
        }
        if (matcher.regionStart() >= matcher.regionEnd()) {
            DocNode docNode = commentEndDocNode;
            commentEndDocNode = null;
            return docNode;
        }
        if (startOfComment) {
            DocNode startDocNode = null;
            int commentEndPos = 0;
            if (matcher.usePattern(COMMENT_START).lookingAt()) {
                commentEndPos = matcher.end();
                startDocNode = createNodeFromMatcher(Type.COMMENT_START);
            }
            commentEndPos = input.indexOf("*/", commentEndPos);
            if (commentEndPos >= 0) {
                matcher.region(matcher.regionStart(), commentEndPos);
                commentEndDocNode = new DocNode(Type.COMMENT_END, TextRange.ofStartAndEnd(input, matcher.regionEnd(), input.length()));
            }
            if (startDocNode != null) {
                return startDocNode;
            }
        }
        if (startOfLine) {
            if (matcher.usePattern(LEAD).lookingAt()) {
                return createNodeFromMatcher(Type.LEAD);
            }
        }
        if (matcher.usePattern(NEWLINE).lookingAt()) {
            return createNodeFromMatcher(Type.NEWLINE);
        }
        if (matcher.usePattern(WHITESPACE).lookingAt()) {
            return createNodeFromMatcher(Type.WHITESPACE);
        }
        return handleTextAndTags();

    }

    private DocNode handleTextAndTags() {
        if (!matcher.usePattern(TEXT).lookingAt()) {
            // This cannot happen. Theoretically.
            throw new AssertionError("lookingAt() false for both NEWLINE and TEXT, which is impossible");
        }        // we're looking at text; this might include tag-related stuff, so this is a bit more complex
        tagMatcher.region(matcher.start(), matcher.end());
        // check for block tag
        if ((startOfLine || postLead) && tagMatcher.usePattern(BLOCK_TAG_START).lookingAt()) {
            inlineTagPatternStack.clear();
            nodeQueue.offer(new DocNode(Type.TEXT,
                    TextRange.ofMatcher(input, tagMatcher, G_TAGNAME)));
            matcher.region(matcher.end(), matcher.regionEnd());
            return new DocNode(Type.BLOCK_TAG_START_MARKER,
                    TextRange.ofMatcher(input, tagMatcher, G_TAGSTART));
        }
        tagMatcher.region(matcher.start(), matcher.end());
        // check for inline tag or the end of an opened inline tag)
        if (!inlineTagPatternStack.isEmpty()) {
            tagMatcher.usePattern(inlineTagPatternStack.peek());

        } else {
            tagMatcher.usePattern(INLINE_TAG_START);
        }
        if (tagMatcher.find()) {
            if (tagMatcher.start() > tagMatcher.regionStart()) {
                nodeQueue.offer(new DocNode(Type.TEXT,
                        TextRange.ofStartAndEnd(input, tagMatcher.regionStart(), tagMatcher.start())));
            }
            if (!inlineTagPatternStack.isEmpty() && tagMatcher.group(G_TAGEND) != null) {
                nodeQueue.offer(new DocNode(Type.INLINE_TAG_END_MARKER,
                        TextRange.ofMatcher(input, tagMatcher, G_TAGEND)));
                inlineTagPatternStack.pop();
            } else {
                switch (tagMatcher.group(G_TAGSTART)) {
                case "{@":
                    inlineTagPatternStack.push(INLINE_TAG_START_OR_END_JAVADOC);
                    break;
                case "{{":
                    inlineTagPatternStack.push(INLINE_TAG_START_OR_END_WIKI);
                    break;
                default:
                    throw new AssertionError("Unreachable code");
                }
                nodeQueue.offer(new DocNode(Type.INLINE_TAG_START_MARKER,
                        TextRange.ofMatcher(input, tagMatcher, G_TAGSTART)));
                nodeQueue.offer(new DocNode(Type.TEXT,
                        TextRange.ofMatcher(input, tagMatcher, G_TAGNAME)));
            }
            matcher.region(tagMatcher.end(), matcher.regionEnd());
            return nodeQueue.poll();
        }
        // we've got an ordinary text match, no specials in here
        return createNodeFromMatcher(Type.TEXT);
    }

    private DocNode createNodeFromMatcher(Type type) {
        DocNode docNode = new DocNode(type, TextRange.ofStartAndEnd(input, matcher.start(), matcher.end()));
        matcher.region(matcher.end(), matcher.regionEnd());
        return docNode;
    }

    @Nullable
    private static Type typeOf(@Nullable DocNode node) {
        return node == null ? null : node.getType();
    }

}
