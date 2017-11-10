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

/**
 * SpaceCharacterRepairSpec contains specification for ... .
 */
@Subject(HtmlEntitiesRepair)
@Unroll
class HtmlEntitiesRepairSpec extends MarkdownRepairSpecBase {
    def "How to encode escaped html entities? - '#markdown'"() {
        given: "a space character repair instance"
        def repairMarkdown = new HtmlEntitiesRepair();

        when: "apply before markdown parser"
        def repaired = repairMarkdown.beforeMarkdownParser(markdown)

        then: "should be replace by {-he-} or not"
        repaired == expected

        where:
        markdown                || expected
        "{&#64;}"               || "{-he-}"
        "{&#x64;}"              || "{-he-}"
        "{&copy;}"              || "{-he-}"
        "{&any thing between;}" || "{-he-}"
        "{-he-}"                || "{-he-}"

        // Not catching
        "{#177;}"               || "{#177;}"  // missing &
        "{&#177}"               || "{&#177}"  // missing ;
        "{#177}"                || "{#177}"  // missing & and ;
    }

    def "How to restore the html entity after applying the MarkdownParser? - '#markdown'"() {
        given: "a space character repair instance (with #storage)"
        def repairMarkdown = new HtmlEntitiesRepair(storage);

        and: "apply the markdown parser"
        def parsed = applyMarkdownParser(markdown)

        when: "apply after markdown parser"
        def repaired = repairMarkdown.afterMarkdownParser(parsed)

        then: "should be corrected"
        repaired == expected

        where:
        markdown       | storage    || expected
        "```{-he-}```" | ["&#64;"]  || "<p><code>&#64;</code></p>"
        "`{-he-}`"     | ["&amp;"]  || "<p><code>&amp;</code></p>"
        "\t{-he-}"     | ["&#xb1;"] || "<pre><code>&#xb1;\n</code></pre>"
        "{-he-}"       | ["&#64;"]  || "<p>&#64;</p>"
        "{-he-}{-he-}" | ["&#64;"]  || "<p>&#64;{-he-}</p>"
    }


    def "How to make html entities useable in markdown code blocks? - '#markdown'"() {
        given: "a space character repair instance"
        def repairMarkdown = new HtmlEntitiesRepair();

        when: "apply before markdown parser"
        def before = repairMarkdown.beforeMarkdownParser(markdown)

        and: "apply the markdown parser"
        def parsed = applyMarkdownParser(before)

        and: "apply after markdown parser"
        def after = repairMarkdown.afterMarkdownParser(parsed)


        then: "should be corrected"
        after == expected

        where:
        markdown           || expected
        "```{&#177;}```"   || "<p><code>&#177;</code></p>"
        "```{&#xb1;}```"   || "<p><code>&#xb1;</code></p>"
        "```{&plusmn;}```" || "<p><code>&plusmn;</code></p>"
        "{&#177;}"         || "<p>&#177;</p>"
        "{&#xb1;}"         || "<p>&#xb1;</p>"
        "{&plusmn;}"       || "<p>&plusmn;</p>"
        "{-he-}{&plusmn;}" || "<p>{-he-}&plusmn;</p>"
        "{#177;}"          || "<p>{#177;}</p>"
    }

}
