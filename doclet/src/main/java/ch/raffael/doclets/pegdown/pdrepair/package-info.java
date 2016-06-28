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

/**
 * # The classes with this package repairs/corrects some issues of using the pegdown parser within javadoc.
 *
 * 1. Correct the leading space issue. *Seems to be no issue anymore, but still in place*. (`SpaceCharacterRepair`)
 * 2. It's not really an issue, but I'd like to strip trailing whitespace characters from every line. (also `SpaceCharacterRepair`)
 * 3. The old implementation of `ch.raffael.doclets.pegdown.Tags` is now part of (`InlineTagletRepair`).
 * This solves the issue with javadoc inline taglets.
 * 4. The @ symbol is always an issue - for example in code blocks for java annotations. Now it's possible to use them without any
 *    further issue. `AtSymbolRepair`
 * 5. Within pegdown/markdown code block, the usage of html entities was not possible, this is fixed by `HtmlEntitiesRepair`.
 *
 * These are all part of `PegdownRepairKit` and are used by `Options.toHtml(java.lang.String, boolean)`.
 *
 * All classes are implementing {@link ch.raffael.doclets.pegdown.pdrepair.PegdownRepair}.
 *
 * @see ch.raffael.doclets.pegdown.Options#toHtml(java.lang.String, boolean)
 * @see ch.raffael.doclets.pegdown.pdrepair.PegdownRepairKit
 * @see ch.raffael.doclets.pegdown.pdrepair.SpaceCharacterRepair
 * @see ch.raffael.doclets.pegdown.pdrepair.InlineTagletRepair
 * @see ch.raffael.doclets.pegdown.pdrepair.AtSymbolRepair
 * @see ch.raffael.doclets.pegdown.pdrepair.HtmlEntitiesRepair
 */
package ch.raffael.doclets.pegdown.pdrepair;