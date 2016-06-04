/*
 * Copyright 2013-2016 Raffael Herzog / Marko Umek
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
 */


package ch.raffael.doclets.pegdown.inlinetag.gist

import ch.raffael.doclets.pegdown.inlinetag.TagContentConverter
import spock.lang.Specification
import spock.lang.Unroll

/**
 * InlineTagRenderCollector contains specification for ... .
 */
class GistTagRenderSpec extends Specification {

    @Unroll
    def "What is the result of rendering '#markup'?"() {
        given: "a gist markdown converter (stub)"
        def markdownConverter = Stub(TagContentConverter) {
            markdown(_) >> { String gistid -> "\ngist ${gistid}\n" }
        }

        and: "a gist renderer"
        def renderer = new GistTagRender(markdownConverter)

        when: "render #markup"
        def markdown=renderer.render(markup)

        then: "should be the expected markdown"
        markdown==expected

        where:
        markup                                            || expected
        "This is normal text"                             || "This is normal text"
        "{@gist 123456}"                                  || "\ngist 123456"
        "This is {@gist 123456} a gist"                   || "This is\ngist 123456\na gist"
        "This is\n{@gist 123456}\na gist"                 || "This is\ngist 123456\na gist"
        "This is\n{@gist 123456}\na gist {@gist 7865431}" || "This is\ngist 123456\na gist\ngist 7865431"
    }

}