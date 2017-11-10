/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
 *
 * This file is part of markdown-doclet.
 *
 * markdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with markdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package ch.raffael.mddoclet.mdrepair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * AtSymbolEscape aims to provide a workaround for `@` signs at the beginning of
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
 * .@starts with `.@` in the source (displayed in HTML as '@')
 * ..@starts with `..@` in the source (displayed in HTML as '.@')
 * ...@starts with `...@` in the source ( (displayed in HTML as '..@')
 * ```
 *
 * @author Raffael Herzog
 */
public class UnescapeAtSymbolRepair extends DefaultMarkdownRepair {

    private final Pattern LITERAL_AT = Pattern.compile("([\r\n]\\s*)\\.(\\.*)@");
    private final String REPLACEMENT = "$1$2@";

    @Override
    public String beforeMarkdownTaglets(String markup) {
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
