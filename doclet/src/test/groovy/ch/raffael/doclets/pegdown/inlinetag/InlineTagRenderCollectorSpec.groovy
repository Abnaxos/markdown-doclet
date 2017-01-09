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


package ch.raffael.doclets.pegdown.inlinetag

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

/**
 * InlineTagRenderCollector contains specification for ... .
 */
@Subject(InlineTagRenderCollector)
class InlineTagRenderCollectorSpec extends Specification {

    public static final String ORIGIN_MARKUP = "origin-markup"

    @Unroll
    def "what happens with registered render(s): #itrs?"() {
        given: "create an InlineTagRender collector"
        def collector = new InlineTagRenderCollector();

        and: "register InlineTagRender(s) (#itrs)"
        itrs.each {
            collector.register(it)
        }

        when: "render the markup"
        def markdown=collector.render(ORIGIN_MARKUP)

        then: "all renders should be applied in registered order (and applied on the previous outcome)"
        markdown==expected

        where:
        itrs                          || expected
        []                            || "${ORIGIN_MARKUP}"
        [itr("1st")]                  || "1st(${ORIGIN_MARKUP})"
        [itr("1st"), itr("2nd")]      || "2nd(1st(${ORIGIN_MARKUP}))"
        [itr("2nd"), itr("1st")]      || "1st(2nd(${ORIGIN_MARKUP}))"
    }

    private InlineTagRender itr(String itrName) {
        return Stub(InlineTagRender) { InlineTagRender inlineTagRender ->
            inlineTagRender.render(_) >> { String markup -> "${itrName}(${markup})"}
            inlineTagRender.toString() >> { "ITR(${itrName})" }
        }
    }
}