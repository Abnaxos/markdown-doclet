/*
 * Copyright 2013 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.raffael.doclets.pegdown;

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
     * **Note:** This implementation may be a bit simplistic:
     *
     * 1. It checks that HTML starts with "`<p>`"
     * 2. It checks that HTML contains any other instances of "`<p>`"
     * 3. It checks that the HTML ends with "`</p>`"
     *
     * If all three conditions are true, the `<p>` tags are removed.
     *
     * @return The HTML without the `<p>` tags or the original HTML.
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
