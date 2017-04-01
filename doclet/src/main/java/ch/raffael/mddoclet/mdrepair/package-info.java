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
 */

/**
 * # The classes with this package repairs/corrects some issues of using a markdown parser with javadoc.
 *
 * 1. Correct the leading space issue. *Seems to be no issue anymore, but still in place*. (`SpaceCharacterRepair`)
 * 2. It's not really an issue, but I'd like to strip trailing whitespace characters from every line. (also `SpaceCharacterRepair`)
 * 3. The old implementation of `ch.raffael.doclets.pegdown.Tags` is now part of (`InlineTagletRepair`).
 * This solves the issue with javadoc inline taglets.
 * 4. The @ symbol is always an issue - for example in code blocks for java annotations. Now it's possible to use them without any
 *    further issue. `AtSymbolRepair`
 * 5. Within markdown code block, the usage of html entities was not possible, this is fixed by `HtmlEntitiesRepair`.
 *
 * These are all part of `MarkdownRepairKit` and are used by `Options.toHtml(java.lang.String, boolean)`.
 *
 * All classes are implementing {@link ch.raffael.mddoclet.mdrepair.MarkdownRepair}.
 *
 * @see ch.raffael.mddoclet.Options#toHtml(java.lang.String, boolean)
 * @see ch.raffael.mddoclet.mdrepair.MarkdownRepairKit
 * @see ch.raffael.mddoclet.mdrepair.SpaceCharacterRepair
 * @see ch.raffael.mddoclet.mdrepair.InlineTagletRepair
 * @see ch.raffael.mddoclet.mdrepair.AtSymbolRepair
 * @see ch.raffael.mddoclet.mdrepair.HtmlEntitiesRepair
 */
package ch.raffael.mddoclet.mdrepair;
