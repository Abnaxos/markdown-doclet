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

import ch.raffael.doclets.pegdown.mdtaglet.MarkdownTaglet;

/**
 * # MarkdownRepair _repairs_ or _corrects_ the markdown/markup.
 *
 * Markdown and Javadoc do not always work smoothly together.
 *
 * All (local) implementation help to solve these obstacles. These local implementations are
 * collected by {@link MarkdownRepairKit} and apply them in the correct order.
 *
 * The order in which the repair methods are called are:
 *
 * 1. {@link #beforeMarkdownTaglets(String)} : This method get the raw markdown/markup before any {@link MarkdownTaglet}
 *      has been executed.
 * 2. {@link #beforeMarkdownParser(String)}  : This method will be called just before the application of markdown parser
 * 3. {@link #afterMarkdownParser(String)} : and finally the generated markup of the markdown parser will be corrected.
 *
 * @see MarkdownRepairKit
 * @see ch.raffael.doclets.pegdown.Options
 */
public interface MarkdownRepair {
    /**
     * Called just before any application of {@link MarkdownTaglet}.
     * @param markdown the (origin) markdown/markup
     * @return the corrected markdown
     */
    String beforeMarkdownTaglets(String markdown);

    /**
     * Applied before the Pegdown parser will be applied.
     * @param markdown the markdown
     * @return the corrected markup.
     */
    String beforeMarkdownParser(String markdown);

    /**
     * Applied after the Pegdown parser has been applied.
     * @param markup the (converted) markup.
     * @return the corrected markup.
     */
    String afterMarkdownParser(String markup);
}
