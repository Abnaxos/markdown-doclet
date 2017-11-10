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
 * MarkdownRepairKitSpec contains specification for ... .
 */
@Subject(MarkdownRepairKit)
@Unroll
class MarkdownRepairKitSpec extends MarkdownRepairSpecBase {
    @SuppressWarnings("GroovyAssignabilityCheck")
    def "How to use all MarkdownRepair instances? - #markdown"() {
        given: "a repair kit"
        def repairKit = new MarkdownRepairKit(stripSpace);

        when: "apply beforeMarkdownParser"
        def markup = repairKit.beforeMarkdownParser(markdown)

        and: "apply markdown parser"
        markup = applyMarkdownParser(markup)

        and: "apply afterMarkdownParser"
        markup = repairKit.afterMarkdownParser(markup)

        then: ""
        markup == expected

        where:
        markdown                                                                            | stripSpace || expected
        'This is a {@link MyClass#anyMethod(String)}'                                       | true       || "<p>This is a {@link MyClass#anyMethod(String)}</p>"
        ' This is a {@link MyClass#anyMethod(String)}'                                      | true       || "<p>This is a {@link MyClass#anyMethod(String)}</p>"
        ' This is a {@link MyClass#anyMethod(String)}'                                      | false      || "<p>This is a {@link MyClass#anyMethod(String)}</p>"
        'This is a {@link MyClass#anyMethod(String)}    '                                   | true       || "<p>This is a {@link MyClass#anyMethod(String)} </p>"
        'This is at symbol @ test'                                                          | true       || "<p>This is at symbol &#64; test</p>"
        '```java\n// This is an annotation test\n@Annotation\n```'                          | true       || "<pre><code class=\"java\">// This is an annotation test\n&#64;Annotation\n</code></pre>"
        'Use html entity &#64; test'                                                        | true       || "<p>Use html entity &#64; test</p>"
        'Use html entity \\&#64; test'                                                      | true       || "<p>Use html entity &amp;#64; test</p>"
        'Use html entity {&#64;} test'                                                      | true       || "<p>Use html entity &#64; test</p>"
        '```\nUse html entity within code block: &amp; &#9985; &#x2703;\n```'               | true       || "<pre><code>Use html entity within code block: &amp;amp; &amp;#9985; &amp;#x2703;\n</code></pre>"
        '```\nUse escaped html entity within code block: {&amp;} {&#9985;} {&#x2703;}\n```' | true       || "<pre><code>Use escaped html entity within code block: &amp; &#9985; &#x2703;\n</code></pre>"
    }
}
