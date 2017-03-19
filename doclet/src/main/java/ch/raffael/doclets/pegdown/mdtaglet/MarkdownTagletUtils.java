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

import java.util.regex.Pattern;

/**
 * MarkdownTagletUtils is an utility class.
 */
public final class MarkdownTagletUtils {
    private static final Pattern STRIP_BLANKS_FROM_LINE_START_PATTERN = Pattern.compile("^\\p{Blank}+", Pattern.MULTILINE);
    private static final Pattern STRIP_BLANKS_FROM_LINE_END_PATTERN = Pattern.compile("\\p{Blank}+$", Pattern.MULTILINE);


    /**
     * Remove blank characters from end of line.
     * @param input the input
     * @return stripped input.
     */
    public static String stripBlanksFromLineEnd(String input) {
        return STRIP_BLANKS_FROM_LINE_END_PATTERN.matcher(input).replaceAll("");
    }
    /**
     * Remove blank characters from start of every line.
     * @param input the input
     * @return stripped input.
     */
    public static String stripBlanksFromLineStart(String input) {
        return STRIP_BLANKS_FROM_LINE_START_PATTERN.matcher(input).replaceAll("");
    }
}
