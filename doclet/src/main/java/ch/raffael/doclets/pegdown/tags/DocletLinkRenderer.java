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

import com.google.common.base.CharMatcher;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.WikiLinkNode;


/**
 * The default link renderer for this doclet. It overrides the rendering of Wiki-Style
 * links to support the following scheme:
 *
 * * `[[http://www.example.com/]]` is rendered as
 *   `<a href="http://www.example.com">www.example.com</a>`
 *
 * * `[[http://www.example.com/ Example]]` is rendered as
 *   `<a href="http://www.example.com">Example</a>`
 *
 * All other links will be rendered by Pegdown's default renderer.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DocletLinkRenderer extends LinkRenderer {

    @Override
    public Rendering render(WikiLinkNode node) {
        String url = node.getText().trim();
        int pos = CharMatcher.WHITESPACE.indexIn(url);
        String text = url;
        if ( pos >= 0 ) {
            text = url.substring(pos).trim();
            url = url.substring(0, pos);
        }
        return new Rendering(url, text);
    }
}
