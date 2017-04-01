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

package ch.raffael.mddoclet.mdtaglet;

import java.util.regex.Pattern;

/**
 * PredefinedWhiteSpacePreserver is responsible for ...
 */
public enum PredefinedWhiteSpacePreserver implements WhiteSpacePreserver {
    KEEP_ALL,
    STRIP_ALL{
        @Override
        public String leading(String whitespaces) {
            return "";
        }

        @Override
        public String trailing(String whitespaces) {
            return "";
        }
    },
    STRIP_NEW_LINES {
        private final Pattern leadingPattern=Pattern.compile("[\n\r]*$");
        private final Pattern trailingPattern=Pattern.compile("^[\n\r]*");

        @Override
        public String leading(String whitespaces) {
            return leadingPattern.matcher(whitespaces).replaceAll("");
        }

        @Override
        public String trailing(String whitespaces) {
            return trailingPattern.matcher(whitespaces).replaceAll("");
        }
    },
    STRIP_BLANKS{
        private final Pattern leadingPattern=Pattern.compile("\\p{Blank}*$");
        private final Pattern trailingPattern=Pattern.compile("^\\p{Blank}*");

        @Override
        public String leading(String whitespaces) {
            return leadingPattern.matcher(whitespaces).replaceAll("");
        }

        @Override
        public String trailing(String whitespaces) {
            return trailingPattern.matcher(whitespaces).replaceAll("");
        }
    };

    @Override
    public String leading(String whitespaces) {
        return whitespaces;
    }

    @Override
    public String trailing(String whitespaces) {
        return whitespaces;
    }
}
