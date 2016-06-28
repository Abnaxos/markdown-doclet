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

/**
 * PegdownRepair provides two methods to _repair_ the markdown/markup of {@link org.pegdown.PegDownProcessor}.
 */
public interface PegdownRepair {
    /**
     * Applied before the Pegdown parser will be applied.
     * @param markdown the markdown
     * @return the corrected markup.
     */
    String beforePegdownParser(String markdown);

    /**
     * Applied after the Pegdown parser has been applied.
     * @param markup the (converted) markup.
     * @return the corrected markup.
     */
    String afterPegdownParser(String markup);
}
