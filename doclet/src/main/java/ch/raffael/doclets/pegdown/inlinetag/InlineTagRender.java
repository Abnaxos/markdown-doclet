/*
 * Copyright 2013-2016 Raffael Herzog / Marko Umek
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
 */
package ch.raffael.doclets.pegdown.inlinetag;

/**
 * InlineTagRender is responsible for render the markup (searching the custom inline tag) and convert it to markdown.
 */
public interface InlineTagRender {
    /**
     * Renders the markup to markdown using a taglets content.
     *
     * @param markup the markup
     * @return the markdown or if there is no taglet the orgin markup will be returned.
     */
    String render(String markup);
}
