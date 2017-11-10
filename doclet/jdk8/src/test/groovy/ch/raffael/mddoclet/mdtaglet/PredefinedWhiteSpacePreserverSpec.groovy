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


package ch.raffael.mddoclet.mdtaglet

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.raffael.mddoclet.mdtaglet.PredefinedWhiteSpacePreserver.*


/**
 * PredefinedWhiteSpacePreserverSpec contains specification for ... .
 */
@Unroll
@Subject(PredefinedWhiteSpacePreserver)
class PredefinedWhiteSpacePreserverSpec extends Specification {

    def "What should happen to leading whitespaces? - using #wsp"() {
        when: "apply whitespaces on #wsp"
        def resultOrig=wsp.leading(whitespaces)

        and: "apply (reversed) whitespaces on #wsp"
        def resultReverse=wsp.leading(whitespaces.reverse())

        then: "#whitespaces --> #expectedOrig"
        resultOrig==expectedOrig

        and: "reverse(#whitespaces) --> #expectedReversed"
        resultReverse==expectedReversed

        where:
        wsp             | whitespaces     || expectedOrig  | expectedReversed
        KEEP_ALL        | "\n\n   \t\t"   || "\n\n   \t\t" | "\n\n   \t\t".reverse()
        STRIP_ALL       | "\n\n   \t\t"   || ""            | ""
        STRIP_BLANKS    | "\n\r\n   \t\t" || "\n\r\n"      | "\n\r\n   \t\t".reverse()
        STRIP_NEW_LINES | "  \t\n\n\r\n"  || "  \t"        | "  \t\n\n\r\n".reverse()
    }

    def "What should happen to trailing whitespaces? - using #wsp?"() {
        when: "apply whitespaces on #wsp"
        def resultOrig=wsp.trailing(whitespaces)

        and: "apply (reversed) whitespaces on #wsp"
        def resultReverse=wsp.trailing(whitespaces.reverse())

        then: "#whitespaces --> #expectedOrig"
        resultOrig==expectedOrig

        and: "reverse(#whitespaces) --> #expectedReversed"
        resultReverse==expectedReversed

        where:
        wsp             | whitespaces     || expectedOrig  | expectedReversed
        KEEP_ALL        | "\n\n   \t\t"   || "\n\n   \t\t" | "\n\n   \t\t".reverse()
        STRIP_ALL       | "\n\n   \t\t"   || ""            | ""
        STRIP_BLANKS    | "   \t\t\n\r\n" || "\n\r\n"      | "   \t\t\n\r\n".reverse()
        STRIP_NEW_LINES | "\n\n\r\n  \t"  || "  \t"        | "\n\n\r\n  \t".reverse()
    }
}
