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

import static ch.raffael.mddoclet.mdrepair.AtSymbolRepair.AT_HTML_ENTITY as AH
import static ch.raffael.mddoclet.mdrepair.AtSymbolRepair.MARKER as M


/**
 * SpaceCharacterRepairSpec contains specification for ... .
 */
@Subject(AtSymbolRepair)
@Unroll
class AtSymbolRepairSpec extends MarkdownRepairSpecBase {
    def "How to encode the @ symbol before applying the MarkdownParser? - '#markdown'"() {
        given: "a space character repair instance"
        def repairMarkdown = new AtSymbolRepair();

        when: "apply before markdown parser"
        def repaired = repairMarkdown.beforeMarkdownParser(markdown)

        then: "should be corrected"
        repaired == expected

        where:
        markdown       || expected
        "@Annotation"  || "{-at-}Annotation"
        "@Ann1\n@Ann2" || "{-at-}Ann1\n{-at-}Ann2"
        "{-at-}"       || "{-at-}"
        "{-at-}@Ann"   || "{-at-}{-at-}Ann"
    }

    def "How to restore the @ symbol after applying the MarkdownParser? - '#markdown'"() {
        given: "a space character repair instance (with #storage)"
        def repairMarkdown = new AtSymbolRepair(storage);

        and: "apply the markdown parser"
        def parsed = applyMarkdownParser(markdown)

        when: "apply after markdown parser"
        def repaired = repairMarkdown.afterMarkdownParser(parsed)

        then: "should use the html entity code for @"
        repaired == expected

        where:
        markdown                 | storage  || expected
        "{-at-}Annotation"       | [AH]     || "<p>&#64;Annotation</p>"
        "{-at-}Annotation"       | [M]      || "<p>{-at-}Annotation</p>"
        "{-at-}Ann1\n{-at-}Ann2" | [AH, AH] || "<p>&#64;Ann1<br/>&#64;Ann2</p>"
        "{-at-}Ann1\n{-at-}Ann2" | [M, AH]  || "<p>{-at-}Ann1<br/>&#64;Ann2</p>"
        "{-at-}Ann1\n{-at-}Ann2" | [AH, M]  || "<p>&#64;Ann1<br/>{-at-}Ann2</p>"
        // It's actually an error
        "{-at-}Ann1\n{-at-}Ann2" | [AH]     || "<p>&#64;Ann1<br/>{-at-}Ann2</p>"
    }


    def "How to handle the @ symbol? - '#markdown'"() {
        given: "a space character repair instance"
        def repairMarkdown = new AtSymbolRepair();

        when: "apply before markdown parser"
        def before = repairMarkdown.beforeMarkdownParser(markdown)

        and: "apply the markdown parser"
        def parsed = applyMarkdownParser(before)

        and: "apply after markdown parser"
        def after = repairMarkdown.afterMarkdownParser(parsed)


        then: "should be corrected"
        after == expected

        where:
        markdown       || expected
        "@Annotation"  || "<p>&#64;Annotation</p>"
        "@Ann1\n@Ann2" || "<p>&#64;Ann1<br/>&#64;Ann2</p>"
        "{-at-}"       || "<p>{-at-}</p>"
        "{-at-}@Ann"   || "<p>{-at-}&#64;Ann</p>"
    }

}
