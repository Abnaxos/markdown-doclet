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
package mdtaglets

import spock.lang.Unroll
/**
 * MarkdownTagletDocletIntegrationSpec tests the integration of {@link ch.raffael.mddoclet.MarkdownDoclet}
 * and {@link ch.raffael.mddoclet.mdtaglet.MarkdownTaglets}.
 */
class MarkdownTagletDocletIntegrationSpec extends MarkdownTagletSpecBase {

    @Unroll
    def "How to integrate a (simple) MarkdownTaglet? - options=#options"() {
        when: "Run javadoc with (sample) HelloTaglet "
        def success = javadocRunner.generateJavadoc([UseHelloTag], options)

        and: "extract the javadoc"
        def javadoc = resolveGeneratedHtmlJavadoc(UseHelloTag)

        then: "javadoc has been successfully generated"
        success

        and: "the markdown taglet has been applied"
        javadoc.select('div.contentContainer div.description div.block').html() == markup

        where:
        options                                                  || markup
        []                                                       || "<h1>Just say {{hello Peter Paul Mary}}.</h1>"
        markdownTaglets(HelloTaglet)                             || "<h1>Just say <em>Hello Peter, Paul, Mary</em>.</h1>"
        markdownTaglets(HelloTaglet) + ["-mdt-hello-lang", "IT"] || "<h1>Just say <em>Ciao Peter, Paul, Mary</em>.</h1>"
    }
}
