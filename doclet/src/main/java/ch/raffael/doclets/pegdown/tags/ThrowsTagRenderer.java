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

import com.sun.javadoc.ThrowsTag;

import ch.raffael.doclets.pegdown.PegdownDoclet;


/**
 * Renderer for `@throws` and `@exception` tags.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ThrowsTagRenderer implements TagRenderer<ThrowsTag> {

    public static final ThrowsTagRenderer INSTANCE = new ThrowsTagRenderer();

    @Override
    public void render(ThrowsTag tag, StringBuilder target, PegdownDoclet doclet) {
        target.append(tag.name())
                .append(' ').append(tag.exceptionName())
                .append(' ').append(TagRendering.simplifySingleParagraph(doclet.toHtml(tag.exceptionComment())));
    }
}
