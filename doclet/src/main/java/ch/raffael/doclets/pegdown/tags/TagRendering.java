/*
 * Copyright 2013 Raffael Herzog
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
package ch.raffael.doclets.pegdown.tags;

/**
 * Utilities for tag rendering.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public final class TagRendering {

    private TagRendering() {
    }

    /**
     * Removes the `<p>` tag if the given HTML contains only one paragraph.
     *
     * **Note:** This implementation may be a bit simplistic: If the HTML starts with a
     * `<p>` tag, it will be removed. If it ends with `</p>`, this one will be removed,
     * too. In 99% of the cases, this *exactly* what we wanted. It some special cases,
     * the result may be invalid HTML -- it should never break things, however, because
     * HTML is designed to handle "forgotten" tags gracefully.
     *
     * @return The HTML without leading `<p>` or trailing `</p>`.
     */
    public static String simplifySingleParagraph(String html) {
        html = html.trim();
        String upper = html.toUpperCase();
        if ( upper.startsWith("<P>") ) {
            html = html.substring(3);
        }
        if ( upper.endsWith("</P>") ) {
            html = html.substring(0, html.length() - 4);
        }
        return html;
    }

}
