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
package ch.raffael.doclets.pegdown;

import org.pegdown.LinkRenderer;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.VerbatimNode;


/**
 * Customises the HTML rendering.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DocletSerializer extends ToHtmlSerializer {

    private final Options options;

    public DocletSerializer(Options options, LinkRenderer linkRenderer) {
        super(linkRenderer);
        this.options = options;
    }

    /**
     * Overrides the default implementation to set the language to "no-highlight" no
     * language is specified. If highlighting is disabled or auto-highlighting is enabled,
     * this method just calls the default implementation.
     *
     * @param node    The AST node.
     */
    @Override
    public void visit(VerbatimNode node) {
        if ( options.isHighlightEnabled() && !options.isAutoHighlightEnabled() && node.getType().isEmpty() ) {
            VerbatimNode noHighlightNode = new VerbatimNode(node.getText(), "no-highlight");
            noHighlightNode.setStartIndex(node.getStartIndex());
            noHighlightNode.setEndIndex(node.getEndIndex());
            super.visit(noHighlightNode);
        }
        else {
            super.visit(node);
        }
    }
}
