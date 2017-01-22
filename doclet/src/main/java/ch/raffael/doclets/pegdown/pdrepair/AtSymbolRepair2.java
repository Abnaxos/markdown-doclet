package ch.raffael.doclets.pegdown.pdrepair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * AtSymbolRepair2 aims to provide a workaround for `@` signs at the beginning of
 * a line: Prepend an `at` at the start of a line with a `.`. To actually start
 * the line with `.@`, just add another dot.
 *
 * *Demo:*
 *
 * ```java
 * .@MyAnnotation
 * class MyClass {
 * }
 * ```
 *
 * And a single dot followed by an at on the beginning of the line:
 *
 * ```
 * .@starts with `.@` in the source
 * ..@starts with `..@` in the source
 * ...@starts with `...@` in the source
 * ```
 *
 * @author Raffael Herzog
 */
public class AtSymbolRepair2 extends DefaultMarkdownRepair {

    private final Pattern LITERAL_AT = Pattern.compile("([\r\n]\\s*)\\.(\\.*)@");
    private final String REPLACEMENT = "$1$2{&#64;}";

    @Override
    public String beforeMarkdownParser(String markup) {
        StringBuffer buf = new StringBuffer();
        Matcher matcher = LITERAL_AT.matcher(markup);
        boolean didMatch = false;
        while ( matcher.find() ) {
            matcher.appendReplacement(buf, REPLACEMENT);
            didMatch = true;
        }
        matcher.appendTail(buf);
        return buf.toString();
    }
}
