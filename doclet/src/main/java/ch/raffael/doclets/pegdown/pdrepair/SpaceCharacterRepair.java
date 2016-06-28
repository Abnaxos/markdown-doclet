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
package ch.raffael.doclets.pegdown.pdrepair;

import java.util.regex.Pattern;

/**
 * SpaceCharacterRepair corrects the space handling.
 *
 * + Removing the trailing space has been necessary, because of an error of Pegdown. Only a <tab> or 4 <space> characters
 *   will be interpreted as a code block.
 * + Not actually an issue, but a little bit cleaner, to strip all trailing whitespace characters from end of line.
 */
final class SpaceCharacterRepair implements PegdownRepair {
    private static final Pattern LINE_START = Pattern.compile("^ ", Pattern.MULTILINE);
    private static final Pattern LINE_END = Pattern.compile("\\p{Blank}+$", Pattern.MULTILINE);

    @Override
    public String beforePegdownParser(String markdown) {
        return stripBlanksFromLineEnd(stripSingleSpaceFromLineStart(markdown));
    }

    private String stripSingleSpaceFromLineStart(String markdown) {
        return LINE_START.matcher(markdown).replaceAll("");
    }

    @Override
    public String afterPegdownParser(String markup) {
        return stripBlanksFromLineEnd(markup);
    }

    private String stripBlanksFromLineEnd(String markup) {
        return LINE_END.matcher(markup).replaceAll("");
    }
}
