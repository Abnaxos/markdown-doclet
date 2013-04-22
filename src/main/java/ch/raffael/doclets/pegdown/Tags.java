package ch.raffael.doclets.pegdown;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;


/**
 * Utility class providing some helpers for working with tags.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class Tags {

    /**
     * The name of text tags in the resulting array of
     * {@link com.sun.javadoc.Doc#inlineTags() Doc.inlineTags()}.
     */
    public static final String TEXT_TAG_NAME = "Text";

    private static final Pattern TAG_RE = Pattern.compile("\\{@[^\\}]*\\}");
    private static final Pattern SUBST_RE = Pattern.compile("\\{@\\}");

    private static final Map<String, String> KINDS = ImmutableMap.<String, String>builder()
            .put("@exception", "@throws")
            .put("@link", "@see")
            .put("@linkplain", "@see")
            .put("@serialData", "@serial")
            .build();

    private Tags() {
    }

    /**
     * Gets the {@link com.sun.javadoc.Tag#kind() kind} of the tag with the given name.
     * Returns the original name if the tag is unknown.
     *
     * @param name    The tag's name.
     *                
     * @return The kind of the tag.
     *
     * @see com.sun.javadoc.Tag#kind()
     */
    public static String kindOf(String name) {
        String kind = KINDS.get(name);
        return kind == null ? name : kind;
    }

    /**
     * Extracts all inline tags from the given comment and saves them in a target list.
     *
     * **Example**
     *
     * ```
     * /**
     *  * Foo {{@literal @}link bar} bar.
     *  *{@literal /}
     * ```
     *
     * becomes
     *
     * ```
     * /**
     *  * Foo {{@literal @}} bar.
     *  *{@literal /}
     * ```
     *
     * and the target list contains the string `"{{@literal @}link bar}"`.
     *
     * @param comment    The comment to extract the inline tags from.
     * @param target     The target list to save the extracted inline tags to.
     *
     * @return The comment with all inline tags replaced with `"{{@literal @}}"`.
     *
     * @see #insertInlineTags(String, java.util.List)
     */
    public static String extractInlineTags(String comment, List<String> target) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = TAG_RE.matcher(comment);
        while ( matcher.find() ) {
            target.add(matcher.group());
            matcher.appendReplacement(result, "{@}");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Re-inserts all inline tags previously extracted using
     * {@link #extractInlineTags(String, java.util.List) extractInlineTags()}. All
     * occurrences of `"{{@literal @}}"` will be replaced by the string at the
     * corresponding index of the list.
     *
     * @param comment    The comment text.
     * @param tags       The list of saved inline tags.
     *
     * @return The String with all inline tags re-inserted.
     *
     * @see #extractInlineTags(String, java.util.List) 
     */
    public static String insertInlineTags(String comment, List<String> tags) {
        StringBuffer result = new StringBuffer();
        Matcher matcher = SUBST_RE.matcher(comment);
        int index = 0;
        while ( matcher.find() ) {
            String tag;
            if ( index < tags.size() ) {
                tag = tags.get(index++);
            }
            else {
                tag = "{@}";
            }
            matcher.appendReplacement(result, tag);
        }
        matcher.appendTail(result);
        return result.toString();
    }

}
