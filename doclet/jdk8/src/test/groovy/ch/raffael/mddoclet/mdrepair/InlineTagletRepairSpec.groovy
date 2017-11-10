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
 * InlineTagletRepairSpec is responsible for ...
 */
@Unroll
@Subject(InlineTagletRepair)
class InlineTagletRepairSpec extends MarkdownRepairSpecBase {


    def "How to mark inline taglets before markdown parser? - #markdown"() {
        given: "a repair instance"
        def repairInlineTags = new InlineTagletRepair();

        when: "apply repair"
        def repaired = repairInlineTags.beforeMarkdownParser(markdown)

        and: "apply the markdown parser"
        def parsed = applyMarkdownParser(repaired)

        then: "the markdown parser must"
        parsed == expected

        where:
        markdown                                                   || expected
        "{@link MyClass#anyMethod(String)}"                        || "<p>{-?-}</p>"
        "{@linkplain MyClass#anyMethod(String) any thing}"         || "<p>{-?-}</p>"
        "{@value #ANY_VALUE}"                                      || "<p>{-?-}</p>"
        "{@inheritDoc}"                                            || "<p>{-?-}</p>"
        "{@literal @}"                                             || "<p>{-?-}</p>"
        "{@code MyClass}"                                          || "<p>{-?-}</p>"
        "{@docRoot}"                                               || "<p>{-?-}</p>"
        "{@custom any value}"                                      || "<p>{-?-}</p>"
        "{@custom text with } within}"                             || "<p>{-?-} within}</p>"
        "Any *text before* {@custom any value} and *after* taglet" || "<p>Any <em>text before</em> {-?-} and <em>after</em> taglet</p>"
        "Any text without taglet"                                  || "<p>Any text without taglet</p>"
        "{@code MyClass}{@value #ANY_VALUE}"                       || "<p>{-?-}{-?-}</p>"
        "{-?-}"                                                    || "<p>{-?-}</p>"
    }

    def "How to restore inline taglets after markdown parser? - #markdown"() {
        given: "a repair instance"
        def repairInlineTags = new InlineTagletRepair(tags);

        and: "apply the markdown parser"
        def parsed = applyMarkdownParser(markdown)

        when: "apply repairInlineTags"
        def restored = repairInlineTags.afterMarkdownParser(parsed)

        then: "the inline tags should be back again"
        restored == expected

        where:
        markdown                                     | tags                                          || expected
        "{-?-}"                                      | ["@link MyClass#anyMethod(String)"]           || "<p>{@link MyClass#anyMethod(String)}</p>"
        "{-?-} within}"                              | ["@custom text with "]                        || "<p>{@custom text with } within}</p>"
        "Any *text before* {-?-} and *after* taglet" | ["@custom any value"]                         || "<p>Any <em>text before</em> {@custom any value} and <em>after</em> taglet</p>"
        "Any text without taglet"                    | []                                            || "<p>Any text without taglet</p>"
        "{-?-}{-?-}"                                 | ["@code MyClass", "@value #ANY_VALUE"]        || "<p>{@code MyClass}{@value #ANY_VALUE}</p>"
        "{-?-}{-?-}{-?-}"                            | ["@code MyClass", "-?-", "@value #ANY_VALUE"] || "<p>{@code MyClass}{-?-}{@value #ANY_VALUE}</p>"

        // Actually an error, but here it's better to be defensive
        "{-?-}{-?-}{-?-}"                            | ["@code MyClass", "@value #ANY_VALUE"]        || "<p>{@code MyClass}{@value #ANY_VALUE}{-?-}</p>"
    }

    def "What happens, if the marker {-?-} will be used within markdown? - #markdown"() {
        given: "a repair instance"
        def markdownRepair = new InlineTagletRepair();

        when: "apply beforeMarkdownParser"
        def markup = markdownRepair.beforeMarkdownParser(markdown)

        and: "apply markdown parser"
        markup = applyMarkdownParser(markup)

        and: "apply afterMarkdownParser"
        markup = markdownRepair.afterMarkdownParser(markup)

        then: ""
        markup == expected

        where:
        markdown                                            || expected
        "This is a {-?-} {@link MyClass#anyMethod(String)}" || "<p>This is a {-?-} {@link MyClass#anyMethod(String)}</p>"
    }
}
