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
package ch.raffael.mddoclet.mdrepair

import ch.raffael.mddoclet.tags.DocletLinkRenderer
import org.pegdown.Extensions
import org.pegdown.PegDownProcessor
import org.pegdown.ToHtmlSerializer
import spock.lang.Specification
/**
 * MarkdownRepairSpecBase contains specification for ... .
 */
abstract class MarkdownRepairSpecBase extends Specification {
    private static PegDownProcessor pegDownProcessor = new PegDownProcessor(Extensions.ALL_WITH_OPTIONALS, 2000)
    private static DocletLinkRenderer linkRenderer = new DocletLinkRenderer()
    private static ToHtmlSerializer htmlSerializer = new ToHtmlSerializer(linkRenderer);

    protected static String applyMarkdownParser(String markdown) {
        new ToHtmlSerializer(linkRenderer).toHtml(pegDownProcessor.parseMarkdown(markdown.toCharArray()))
    }
}
