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
package ch.raffael.doclets.pegdown.pdrepair

import spock.lang.Subject
import spock.lang.Unroll

@Subject(SpaceCharacterRepair)
@Unroll
class SpaceCharacterRepairSpec extends PegdownRepairSpecBase {
    def "How to strip a single leading space for each line? - '#markdown'"() {
        given: "a space character repair instance"
        def repairPegdown = new SpaceCharacterRepair();

        when: "apply before any markdown taglet"
        def repaired = repairPegdown.beforeMarkdownTaglets(markdown)

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
        def repairPegdown = new SpaceCharacterRepair();

        when: "apply after pegdown parser"
        def repaired = repairPegdown.afterMarkdownParser(markdown)

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