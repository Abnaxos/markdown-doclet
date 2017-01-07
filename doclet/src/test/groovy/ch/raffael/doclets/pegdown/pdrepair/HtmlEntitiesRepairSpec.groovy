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

/**
 * SpaceCharacterRepairSpec contains specification for ... .
 */
@Subject(HtmlEntitiesRepair)
@Unroll
class HtmlEntitiesRepairSpec extends PegdownRepairSpecBase {
    def "How to encode escaped html entities? - '#markdown'"() {
        given: "a space character repair instance"
        def repairPegdown = new HtmlEntitiesRepair();

        when: "apply before pegdown parser"
        def repaired = repairPegdown.beforeMarkdownParser(markdown)

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

    def "How to restore the html entity after applying the PegdownParser? - '#markdown'"() {
        given: "a space character repair instance (with #storage)"
        def repairPegdown = new HtmlEntitiesRepair(storage);

        and: "apply the pegdown parser"
        def parsed = applyPegdownParser(markdown)

        when: "apply after pegdown parser"
        def repaired = repairPegdown.afterMarkdownParser(parsed)

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
        def repairPegdown = new HtmlEntitiesRepair();

        when: "apply before pegdown parser"
        def before = repairPegdown.beforeMarkdownParser(markdown)

        and: "apply the pegdown parser"
        def parsed = applyPegdownParser(before)

        and: "apply after pegdown parser"
        def after = repairPegdown.afterMarkdownParser(parsed)


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