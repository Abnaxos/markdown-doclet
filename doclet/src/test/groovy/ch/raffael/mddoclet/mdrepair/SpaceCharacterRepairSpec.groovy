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

import spock.lang.Subject
import spock.lang.Unroll

@Subject(SpaceCharacterRepair)
@Unroll
class SpaceCharacterRepairSpec extends MarkdownRepairSpecBase {
    def "How to strip a single leading space for each line? - '#markdown'"() {
        given: "a space character repair instance"
        def repairMarkdown = new SpaceCharacterRepair();

        when: "apply before any markdown taglet"
        def repaired = repairMarkdown.beforeMarkdownTaglets(markdown)

        then: "should be corrected"
        repaired == expected

        where:
        markdown           || expected
        "Line 1"           || "Line 1"
        " Line 1"          || "Line 1"
        "  Line 1"         || " Line 1"
        "| Line 1"         || "| Line 1"
        " Line 1\n Line 2" || "Line 1\nLine 2"
    }

    @Unroll
    def "How to strip trailing blank characters for each line? - '#markdown'"() {
        given: "a space character repair instance"
        def repairMarkdown = new SpaceCharacterRepair();

        when: "apply after markdown parser"
        def repaired = repairMarkdown.afterMarkdownParser(markdown)

        then: "should be corrected"
        repaired == expected

        where:
        markdown                 || expected
        "Line 1   "              || "Line 1"
        "Line 1\t\t"             || "Line 1"
        " Line 1  "              || " Line 1"
        "Line 1    \nLine 2    " || "Line 1\nLine 2"
    }
}
