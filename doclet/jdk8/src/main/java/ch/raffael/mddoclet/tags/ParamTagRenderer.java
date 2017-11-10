/*
 * Copyright 2013 Raffael Herzog
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
 */
package ch.raffael.mddoclet.tags;

import com.sun.javadoc.ParamTag;

import ch.raffael.mddoclet.MarkdownDoclet;


/**
 * Renderer for `@param` tags.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ParamTagRenderer implements TagRenderer<ParamTag> {

    public static final ParamTagRenderer INSTANCE = new ParamTagRenderer();

    @Override
    public void render(ParamTag tag, StringBuilder target, MarkdownDoclet doclet) {
        target.append(tag.name())
                .append(' ').append(renderParameterName(tag))
                .append(' ').append(TagRendering.simplifySingleParagraph(doclet.toHtml(tag.parameterComment())));
    }

    private static String renderParameterName(ParamTag tag) {
        if (!tag.isTypeParameter()) {
            return tag.parameterName();
        }
        else {
            return '<' + tag.parameterName() + '>';
        }
    }
}
