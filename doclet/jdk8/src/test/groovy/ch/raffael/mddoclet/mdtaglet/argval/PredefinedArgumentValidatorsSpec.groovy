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


package ch.raffael.mddoclet.mdtaglet.argval

import ch.raffael.mddoclet.mdtaglet.ArgumentValidator
import ch.raffael.mddoclet.mdtaglet.ValidationResult
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.raffael.mddoclet.mdtaglet.ArgumentValidator.*
import static ch.raffael.mddoclet.mdtaglet.argval.PredefinedArgumentValidators.*

@Unroll
@Subject([ArgumentValidator, PredefinedArgumentValidators])
class PredefinedArgumentValidatorsSpec extends Specification {

    private static final String VALID_ERROR_MSG = ""
    private static final String ANY_ERROR = "any error description"

    def "What are the predefined number of arguments validators? - (#av) applied on (#args) -> #expected"() {
        when: "validate #args"
        ValidationResult result = av.validate args

        then: "should have the correct ValidationResult Type"
        result == expected

        and: "the expected message"
        result.error == expectedError

        where:
        av           | args    || expected              | expectedError
        NO_ARGUMENTS | list(0) || VR_VALID              | VALID_ERROR_MSG
        NO_ARGUMENTS | list(1) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected no argument(s), got 1!")
        ZERO_OR_MORE | list(0) || VR_VALID              | VALID_ERROR_MSG
        ZERO_OR_MORE | list(3) || VR_VALID              | VALID_ERROR_MSG
        ONE_OR_MORE  | list(0) || VR_MISSING_ARGUMENTS  | ValidationResultHelper.errMissing("Expected 1 or more argument(s), got 0!")
        ONE_OR_MORE  | list(1) || VR_VALID              | VALID_ERROR_MSG
        ONE_OR_MORE  | list(4) || VR_VALID              | VALID_ERROR_MSG
        TWO_OR_MORE  | list(1) || VR_MISSING_ARGUMENTS  | ValidationResultHelper.errMissing("Expected 2 or more argument(s), got 1!")
        TWO_OR_MORE  | list(2) || VR_VALID              | VALID_ERROR_MSG
        TWO_OR_MORE  | list(3) || VR_VALID              | VALID_ERROR_MSG
        ZERO_OR_ONE  | list(0) || VR_VALID              | VALID_ERROR_MSG
        ZERO_OR_ONE  | list(1) || VR_VALID              | VALID_ERROR_MSG
        ZERO_OR_ONE  | list(3) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected 0 or 1 argument(s), got 3!")
        EXACTLY_ONE  | list(0) || VR_MISSING_ARGUMENTS  | ValidationResultHelper.errMissing("Expected exactly 1 argument(s), got 0!")
        EXACTLY_ONE  | list(1) || VR_VALID              | VALID_ERROR_MSG
        EXACTLY_ONE  | list(3) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected exactly 1 argument(s), got 3!")
        EXACTLY_TWO  | list(1) || VR_MISSING_ARGUMENTS  | ValidationResultHelper.errMissing("Expected exactly 2 argument(s), got 1!")
        EXACTLY_TWO  | list(2) || VR_VALID              | VALID_ERROR_MSG
        EXACTLY_TWO  | list(4) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected exactly 2 argument(s), got 4!")
    }

    def "How to define custom number of arguments validator - (#av) applied on (#args) -> #expected?"() {
        when: "validate #args"
        ValidationResult result = av.validate args

        then: "should have the correct ValidationResult Type"
        result == expected

        and: "the expected message"
        result.error == expectedError

        where:
        av            | args    || expected | expectedError
        atLeast(7)    | list(7) || VR_VALID | VALID_ERROR_MSG
        atLeast(7)    | list(9) || VR_VALID | VALID_ERROR_MSG
        atLeast(7)    | list(6) || VR_MISSING_ARGUMENTS | ValidationResultHelper.errMissing("Expected 7..* argument(s), got 6!")

        atMost(7)     | list(7) || VR_VALID | VALID_ERROR_MSG
        atMost(7)     | list(6) || VR_VALID | VALID_ERROR_MSG
        atMost(7)     | list(9) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected 0..7 argument(s), got 9!")

        inRange(5, 7) | list(5) || VR_VALID | VALID_ERROR_MSG
        inRange(5, 7) | list(7) || VR_VALID | VALID_ERROR_MSG
        inRange(5, 7) | list(9) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected 5..7 argument(s), got 9!")
        inRange(5, 7) | list(4) || VR_MISSING_ARGUMENTS | ValidationResultHelper.errMissing("Expected 5..7 argument(s), got 4!")
        inRange(0, 7) | list(9) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected 0..7 argument(s), got 9!")

        exactly(7)    | list(7) || VR_VALID | VALID_ERROR_MSG
        exactly(7)    | list(6) || VR_MISSING_ARGUMENTS | ValidationResultHelper.errMissing("Expected exactly 7 argument(s), got 6!")
        exactly(7)    | list(8) || VR_TOO_MANY_ARGUMENTS | ValidationResultHelper.errTooMany("Expected exactly 7 argument(s), got 8!")
    }

    /**
     * Creates a constant {@link ArgumentValidator} to return always the same {@link ValidationResult}.
     *
     * @param validationResult the validationResult to be returned when called by {@link ArgumentValidator#validate(java.util.List)}.
     * @return an constant ArgumentValidator
     */
    private static ArgumentValidator constant(ValidationResult validationResult) {
        return { List<String> args -> validationResult } as ArgumentValidator
    }

    private static final ArgumentValidator AV_VALID = constant(VR_VALID);
    private static final ArgumentValidator AV_MISS = constant(VR_MISSING_ARGUMENTS);
    private static final ArgumentValidator AV_MANY = constant(VR_TOO_MANY_ARGUMENTS);
    private static final ArgumentValidator AV_TYPE = constant(VR_TYPE_MISMATCH);

    def "How to express logical expressions - (#av)?"() {
        when: "validate #args"
        ValidationResult result = av.validate list(7)

        then: "should have the correct ValidationResult Type"
        result == expected

        where:
        av                                                  || expected
        allOf("allOf(VALID)", AV_VALID)                     || VR_VALID
        allOf("allOf(VALID, VALID)", AV_VALID, AV_VALID)    || VR_VALID
        allOf("allOf(VALID, NOT VALID)", AV_VALID, AV_MISS) || VR_MISSING_ARGUMENTS
        allOf("allOf(NOT VALID, VALID)", AV_MISS, AV_VALID) || VR_MISSING_ARGUMENTS
        allOf("allOf(MANY, MISS)", AV_MANY, AV_MISS)        || VR_TOO_MANY_ARGUMENTS
        allOf("allOf(TYPE, MISS)", AV_TYPE, AV_MISS)        || VR_TYPE_MISMATCH
        allOf("allOf()")                                    || VR_INVALID_VALIDATOR

        anyOf("anyOf(VALID)", AV_VALID)                     || VR_VALID
        anyOf("anyOf(VALID, VALID)", AV_VALID, AV_VALID)    || VR_VALID
        anyOf("anyOf(NOT VALID, VALID)", AV_MISS, AV_VALID) || VR_VALID
        anyOf("anyOf(VALID, NOT VALID)", AV_VALID, AV_MISS) || VR_VALID
        anyOf("anyOf(MANY, MISS)", AV_MANY, AV_MISS)        || VR_TOO_MANY_ARGUMENTS
        anyOf("anyOf(TYPE, MISS)", AV_TYPE, AV_MISS)        || VR_TYPE_MISMATCH
        anyOf("anyOf()")                                    || VR_INVALID_VALIDATOR
    }

    /**
     * Creates a list predicate which return always `valid`.
     * @param valid
     * @return
     */
    private static ArgumentListPredicate argumentListPredicate(boolean valid) {
        return { list -> valid } as ArgumentListPredicate
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "How to validate the type of the arguments - Predicate applied on (#args) returns #predResult?"() {
        given: "an arguments type validator using a argument list predicate"
        ArgumentValidator validator = argumentTypesValidator(ANY_ERROR, argumentListPredicate(predResult))

        when: "validate #args"
        ValidationResult result = validator.validate args

        then: "should validate to #expected"
        result == expected

        and: "the error message"
        result.error == expectedError

        where:
        predResult | args    || expected | expectedError
        true       | list(1) || VR_VALID | ""
        false      | list(1) || VR_TYPE_MISMATCH | ValidationResultHelper.errTypeMismatch(ANY_ERROR)
        // independent of list size!!
        true       | list(0) || VR_VALID | ""
        false      | list(0) || VR_TYPE_MISMATCH | ValidationResultHelper.errTypeMismatch(ANY_ERROR)
    }

    def "How to validate each argument with a single predicate - Predicate applied on (#args) returns #expected?"() {
        given: "a string predicate (which checks for numbers)"
        ArgumentPredicate predicate = { String arg -> arg.isNumber() } as ArgumentPredicate

        and: "a argument type validator"
        ArgumentValidator validator = argumentTypeValidator("Hey! Only numbers expected", predicate)

        when: "applying the validator on #args"
        ValidationResult result = validator.validate args

        then: "should validate to #expected"
        result == expected

        and: "the error message should be '#expectedError'"
        result.error == expectedError

        where:
        args                  || expected | expectedError
        list("1", "2", "3")   || VR_VALID | ""
        list()                || VR_VALID | ""
        list("1", "2", "X")   || VR_TYPE_MISMATCH | ValidationResultHelper.errTypeMismatch("Hey! Only numbers expected. Argument \"X\" not accepted!")
        list("1st", "2", "X") || VR_TYPE_MISMATCH | ValidationResultHelper.errTypeMismatch("Hey! Only numbers expected. Argument \"1st\" not accepted!")

    }

    /**
     * Creates a list.
     * @param the arguments
     * @return an argument list
     */
    private static List<String> list(String... args) {
        return args.toList();
    }

    /**
     * Creates a list with expected number of arguments
     * @param numArgs the number of arguments
     * @return a list with expected number of arguments [1..numArgs]
     */
    private static List<String> list(int numArgs) {
        def list = new ArrayList(numArgs);
        for (int value = 1; value <= numArgs; value++) {
            list.add(value.toString())
        }
        assert list.size() == numArgs: "list(numArgs) does not create list with " + numArgs + " arguments!"
        return list;
    }
}
