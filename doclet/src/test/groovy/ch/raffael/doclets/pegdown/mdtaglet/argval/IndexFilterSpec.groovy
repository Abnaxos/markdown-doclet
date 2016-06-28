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


package ch.raffael.doclets.pegdown.mdtaglet.argval

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.raffael.doclets.pegdown.mdtaglet.argval.IndexFilter.*
import static java.lang.Integer.MAX_VALUE
import static java.lang.Integer.MIN_VALUE

@Unroll
@Subject([IndexFilter])
@SuppressWarnings("GroovyPointlessBoolean")
class IndexFilterSpec extends Specification {

    def "How to specify specific index? - #ip(#idx) -> #expected"() {
        expect:
        expected == ip.filter(idx)

        where:
        ip       | idx || expected
        at(7)    | 7   || true
        at(7)    | 3   || false
        at(0, 7) | 0   || true
        at(0, 7) | 7   || true
        at(0, 7) | 3   || false
        at(7, 0) | 0   || true
        at(7, 0) | 7   || true
        at()     | 0   || false
        at()     | 1   || false
    }

    def "How to specify a range of valid indices? - #ip(#idx) -> #expected"() {
        expect:
        expected == ip.filter(idx)

        where:
        ip          | idx || expected
        range(1, 3) | 1   || true
        range(1, 3) | 2   || true
        range(1, 3) | 3   || true
        range(1, 3) | 0   || false
        range(1, 3) | 4   || false
    }

    def "How to specify valid indices up to max value? - #ip(#idx) -> #expected"() {
        expect:
        expected == ip.filter(idx)

        where:
        ip     | idx       || expected
        max(3) | 0         || true
        max(3) | 1         || true
        max(3) | 2         || true
        max(3) | 3         || true
        max(3) | 4         || false
        max(3) | -1        || false
        max(3) | MIN_VALUE || false
    }

    def "How to specify valid indices starting with min value? - #ip(#idx) -> #expected"() {
        expect:
        expected == ip.filter(idx)

        where:
        ip     | idx || expected
        min(3) | 0   || false
        min(3) | 1   || false
        min(3) | 2   || false
        min(3) | 3   || true
        min(3) | 4   || true
    }

    def "How to specify index predicate which is always true for any valid index? -  #ip(#idx) -> #expected"() {
        expect:
        expected == ip.filter(idx)

        where:
        ip    | idx       || expected
        all() | 0         || true
        all() | 10        || true
        all() | MAX_VALUE || true
        all() | -1        || false
        all() | -10       || false
        all() | MIN_VALUE || false
    }
}