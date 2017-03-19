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
import spock.lang.Title
import spock.lang.Unroll

import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentPredicates.*
import static ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentPredicates.Radix.*

/**
 * PredefinedArgumentPredicatesSpec is responsible for ...
 */
@Unroll
@Subject([PredefinedArgumentPredicates, ArgumentPredicate])
@SuppressWarnings("GroovyPointlessBoolean")
@Title("What are the predefined ArgumentPredicates?")
class PredefinedArgumentPredicatesSpec extends Specification {

    def "What are the predefined string (regular expression) based ArgumentPredicates? - #argumentPredicate applied on '#arg' --> #expected"() {
        expect:
        expected == argumentPredicate.test(arg)

        where:
        argumentPredicate         | arg                || expected
        regex("\\d+")             | "1234"             || true
        regex("\\d+")             | "abcd"             || false
        regex("(a|b|c|d)+")       | "abcd"             || true
        regex("(a|b|c|d)+")       | "1234"             || false
        regex("")                 | ""                 || true
        regex("")                 | "1234"             || false

        options("o1", "o2", "o3") | "o1"               || true
        options("o1", "o2", "o3") | "o2"               || true
        options("o1", "o2", "o3") | "o3"               || true
        options("o1", "o2", "o3") | "o0"               || false
        options("o1", "o2", "o3") | "x"                || false
        options("o1", "o2", "o3") | ""                 || false
        options()                 | ""                 || true
        options()                 | "x"                || false

        isEqual("const")          | "const"            || true
        isEqual("const")          | "any-other"        || false
        isEqual("const")          | ""                 || false

        isInteger(DECIMAL)        | "1234567890"       || true
        isInteger(DECIMAL)        | "-1234"            || true
        isInteger(DECIMAL)        | "+1234"            || true
        isInteger(DECIMAL)        | "+ 1234"           || false
        isInteger(DECIMAL)        | "- 1234"           || false
        isInteger(DECIMAL)        | "12af"             || false
        isInteger(DECIMAL)        | "12AF"             || false
        isInteger(DECIMAL)        | ""                 || false

        isInteger(HEXADECIMAL)    | "1234567890abcdef" || true
        isInteger(HEXADECIMAL)    | "1234567890ABCDEF" || true
        isInteger(HEXADECIMAL)    | "1234"             || true
        isInteger(HEXADECIMAL)    | "-1234"            || true
        isInteger(HEXADECIMAL)    | ""                 || false

        isInteger(OCTAL)          | "12345670"         || true
        isInteger(OCTAL)          | "-12345"           || true
        isInteger(OCTAL)          | "891"              || false

        isInteger(BINARY)         | "01011000"         || true
        isInteger(BINARY)         | "-01011000"        || true
        isInteger(BINARY)         | "1234"             || false
    }

    def "What are the predefined integer based ArgumentPredicates? - #argumentPredicate applied on '#arg' --> #expected"() {
        expect:
        expected == argumentPredicate.test(arg)

        where:
        argumentPredicate              | arg    || expected
        inRange(1, 3)                  | "0"    || false
        inRange(1, 3)                  | "1"    || true
        inRange(1, 3)                  | "2"    || true
        inRange(1, 3)                  | "3"    || true
        inRange(1, 3)                  | "4"    || false
        inRange(1, 3)                  | "+3"   || true
        inRange(-1, -1)                | "-1"   || true
        inRange(-1, -1)                | "- 1"  || false
        inRange(1, 3)                  | "-1"   || false
        inRange(1, 3)                  | "ab"   || false
        inRange(1, 3)                  | "any"  || false
        inRange(1, 3)                  | ""     || false

        inRange(1, 3, DECIMAL)         | "0"    || false
        inRange(1, 3, DECIMAL)         | "1"    || true
        inRange(1, 3, DECIMAL)         | "2"    || true
        inRange(1, 3, DECIMAL)         | "3"    || true
        inRange(1, 3, DECIMAL)         | "4"    || false

        inRange(1, 3, BINARY)          | "0"    || false
        inRange(1, 3, BINARY)          | "01"   || true
        inRange(1, 3, BINARY)          | "10"   || true
        inRange(1, 3, BINARY)          | "11"   || true
        inRange(1, 3, BINARY)          | "100"  || false
        inRange(1, 3, BINARY)          | "+10"  || true
        inRange(-1, -1, BINARY)        | "-1"   || true
        inRange(-1, -1, BINARY)        | "- 1"  || false
        inRange(1, 3, BINARY)          | "2"    || false
        inRange(1, 3, BINARY)          | "3"    || false
        inRange(1, 3, BINARY)          | "any"  || false
        inRange(1, 3, BINARY)          | ""     || false

        inRange(1, 10, OCTAL)          | "0"    || false
        inRange(1, 10, OCTAL)          | "1"    || true
        inRange(1, 10, OCTAL)          | "2"    || true
        inRange(1, 10, OCTAL)          | "12"   || true
        inRange(1, 10, OCTAL)          | "13"   || false
        inRange(1, 10, OCTAL)          | "+12"  || true
        inRange(-10, -10, OCTAL)       | "-12"  || true
        inRange(-10, -10, OCTAL)       | "- 12" || false
        inRange(1, 10, OCTAL)          | "8"    || false
        inRange(1, 10, OCTAL)          | "any"  || false
        inRange(1, 10, OCTAL)          | ""     || false

        inRange(1, 20, HEXADECIMAL)    | "0"    || false
        inRange(1, 20, HEXADECIMAL)    | "1"    || true
        inRange(1, 20, HEXADECIMAL)    | "14"   || true
        inRange(1, 20, HEXADECIMAL)    | "15"   || false
        inRange(1, 20, HEXADECIMAL)    | "a"    || true
        inRange(1, 20, HEXADECIMAL)    | "A"    || true
        inRange(1, 20, HEXADECIMAL)    | "f"    || true
        inRange(1, 20, HEXADECIMAL)    | "F"    || true
        inRange(1, 20, HEXADECIMAL)    | "+14"  || true
        inRange(-10, -10, HEXADECIMAL) | "-a"   || true
        inRange(-10, -10, HEXADECIMAL) | "- a"  || false
        inRange(1, 20, HEXADECIMAL)    | "20"   || false
        inRange(1, 20, HEXADECIMAL)    | "any"  || false
        inRange(1, 20, HEXADECIMAL)    | ""     || false

        min(10)                        | "10"   || true
        min(10)                        | "11"   || true
        min(10)                        | "9"    || false

        min(10, DECIMAL)               | "10"   || true
        min(10, DECIMAL)               | "11"   || true
        min(10, DECIMAL)               | "9"    || false

        min(10, BINARY)                | "1010" || true
        min(10, BINARY)                | "1011" || true
        min(10, BINARY)                | "1001" || false

        min(10, OCTAL)                 | "12"   || true
        min(10, OCTAL)                 | "13"   || true
        min(10, OCTAL)                 | "10"   || false

        min(17, HEXADECIMAL)           | "11"   || true
        min(17, HEXADECIMAL)           | "12"   || true
        min(17, HEXADECIMAL)           | "10"   || false

        max(10)                        | "-1"   || true
        max(10)                        | "9"    || true
        max(10)                        | "10"   || true
        max(10)                        | "11"   || false

        max(10, DECIMAL)               | "-1"   || true
        max(10, DECIMAL)               | "9"    || true
        max(10, DECIMAL)               | "10"   || true
        max(10, DECIMAL)               | "11"   || false

        max(10, BINARY)                | "-1"   || true
        max(10, BINARY)                | "1001" || true
        max(10, BINARY)                | "1010" || true
        max(10, BINARY)                | "1011" || false

        max(10, OCTAL)                 | "-1"   || true
        max(10, OCTAL)                 | "11"   || true
        max(10, OCTAL)                 | "12"   || true
        max(10, OCTAL)                 | "13"   || false

        max(17, HEXADECIMAL)           | "-1"   || true
        max(17, HEXADECIMAL)           | "10"   || true
        max(17, HEXADECIMAL)           | "11"   || true
        max(17, HEXADECIMAL)           | "12"   || false
    }
}