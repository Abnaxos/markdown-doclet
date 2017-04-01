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

package ch.raffael.mddoclet.mdtaglet.argval;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Predicate;

import ch.raffael.mddoclet.mdtaglet.ArgumentValidator;
import ch.raffael.mddoclet.mdtaglet.MarkdownTaglet;
import ch.raffael.mddoclet.mdtaglet.ValidationResult;
import ch.raffael.mddoclet.mdtaglet.ValidationResult.Type;

import static ch.raffael.mddoclet.mdtaglet.ValidationResult.internalError;
import static ch.raffael.mddoclet.mdtaglet.ValidationResult.missingArguments;
import static ch.raffael.mddoclet.mdtaglet.ValidationResult.tooManyArguments;
import static ch.raffael.mddoclet.mdtaglet.ValidationResult.typeMismatch;

/**
 * # PredefinedArgumentValidators is the utility class, to create {@link ArgumentValidator} instances.
 *
 * These could be used for implementation of {@link MarkdownTaglet#getArgumentValidator()}.
 *
 * 1. Predefined {@link ArgumentValidator}
 *      * {@link #NO_ARGUMENTS}:  no arguments are expected
 *      * {@link #ZERO_OR_ONE}:   0 or 1 argument is expected
 *      * {@link #ZERO_OR_MORE}:  any number of arguments is ok.
 *      * {@link #ONE_OR_MORE}:   at least 1 argument is expected
 *      * {@link #TWO_OR_MORE}:   at least 2 arguments are expected
 *      * {@link #EXACTLY_ONE}:   exactly 1 argument is expected
 *      * {@link #EXACTLY_TWO}:   exactly 2 argument are expected
 *
 * 2. Some factory methods which checks the number of arguments like
 *      * {@link #atLeast(int)} and {@link #atLeast(int, String)}
 *      * {@link #atMost(int)} and {@link #atMost(int, String)}
 *      * {@link #inRange(int, int)} and {@link #inRange(int, int, String)}
 *      * {@link #exactly(int)} and {@link #exactly(int, String)}
 *
 *
 * 3. And some to create type validators
 *      * {@link #argumentTypesValidator(String, ArgumentListPredicate)}
 *      * {@link #argumentTypeValidator(String, ArgumentPredicate)}
 *      * {@link #argumentTypeValidator(String, IndexFilter, ArgumentPredicate)}
 *
 * 4. logical operators
 *      * {@link #allOf(String, ArgumentValidator...)}: which evaluates to VALID, when each validates to `VALID`
 *      * {@link #anyOf(String, ArgumentValidator...)}: which evaluates to VALID, when any validates to `VALID`
 *
 * @see ArgumentValidator
 * @see MarkdownTaglet#getArgumentValidator()
 * @see ArgumentListPredicate
 * @see PredefinedArgumentPredicates
 * @see IndexFilter
 */
@SuppressWarnings({"Duplicates", "WeakerAccess", "unused"})
public abstract class PredefinedArgumentValidators {

    private PredefinedArgumentValidators() {
        throw new UnsupportedOperationException("It's a utility class. Do not create an instance.");
    }

    /**
     * Max 1 argument.
     */
    private static final ArgumentValidator _AT_MOST_ONE = new TooManyArgumentsValidator(1,"0 or 1");

    /**
     * Max 2 arguments.
     */
    private static final ArgumentValidator _AT_MOST_TWO = new TooManyArgumentsValidator(2, "0 to 2");

    /**
     * zero or more
     */
    public static final ArgumentValidator ZERO_OR_MORE = new MissingArgumentsValidator(0, "any number of");

    /**
     * one or more
     */
    public static final ArgumentValidator ONE_OR_MORE = new MissingArgumentsValidator(1,"1 or more");

    /**
     * 2 or more
     */
    public static final ArgumentValidator TWO_OR_MORE = new MissingArgumentsValidator(2,"2 or more");

    /**
     * no arguments at all.
     */
    public static final ArgumentValidator NO_ARGUMENTS = new TooManyArgumentsValidator(0,"no");


    /**
     * zero or one argument.
     */
    public static final ArgumentValidator ZERO_OR_ONE = inRange(0, 1, "0 or 1");

    /**
     * Exactly one argument expected.
     */
    public static final ArgumentValidator EXACTLY_ONE = exactly(1);

    /**
     * Exactly two arguments expected.
     */
    public static final ArgumentValidator EXACTLY_TWO = exactly(2);


    /**
     * Creates a {@link ArgumentValidator} which checks for _#arguments >= expectedMin_
     *
     * @param expectedMin the minimum number of expected arguments
     * @return the MissingArgumentsValidator
     */
    public static ArgumentValidator atLeast(final int expectedMin) {
        switch (expectedMin) {
            case 0:
                return ZERO_OR_MORE;
            case 1:
                return ONE_OR_MORE;
            case 2:
                return TWO_OR_MORE;
        }
        return atLeast(expectedMin, MessageFormat.format("{0}..*", expectedMin));
    }

    /**
     * Creates a {@link ArgumentValidator} which checks for _#arguments >= expectedMin_
     *
     * @param expectedMin the minimum number of expected arguments
     * @param description any description
     * @return the MissingArgumentsValidator
     */
    public static ArgumentValidator atLeast(final int expectedMin, String description) {
        return new MissingArgumentsValidator(expectedMin, description);
    }

    /**
     * Creates a {@link ArgumentValidator} which checks for _#arguments <= expectedMax_
     *
     * @param expectedMax the maximum number of expected arguments
     * @return the TooManyArgumentsValidator
     */
    public static ArgumentValidator atMost(final int expectedMax) {
        switch (expectedMax) {
            case 0:
                return NO_ARGUMENTS;
            case 1:
                return _AT_MOST_ONE;
            case 2:
                return _AT_MOST_TWO;
        }
        return atMost(expectedMax, MessageFormat.format("0..{0}", expectedMax));
    }

    /**
     * # Creates a {@link ArgumentValidator} which checks for _#arguments <= expectedMax_.
     *
     * @param expectedMax the maximum number of expected arguments
     * @param description any description
     * @return the TooManyArgumentsValidator
     */
    public static ArgumentValidator atMost(final int expectedMax, String description) {
        return new TooManyArgumentsValidator(expectedMax, description);
    }

    /**
     * # Creates a {@link ArgumentValidator} which checks for _#arguments == expected_.
     *
     * @param expected the expected number of arguments
     * @return an ArgumentValidator
     */
    public static ArgumentValidator exactly(int expected) {
        return exactly(expected, "exactly " + expected);
    }

    /**
     * # Creates a {@link ArgumentValidator} which checks for _#arguments == expected_.
     *
     * @param expected the expected number of arguments
     * @param description any description
     * @return an ArgumentValidator
     */
    public static ArgumentValidator exactly(int expected, String description) {
        return inRange(expected, expected, description);
    }

    /**
     * Creates a {@link ArgumentValidator} which checks for _expectedMin <= #arguments <= expectedMax_.
     *
     * @param expectedMin the minimum number of expected arguments
     * @param expectedMax the maximum number of expected arguments
     * @return an ArgumentValidator
     */
    public static ArgumentValidator inRange(int expectedMin, int expectedMax) {
        return inRange(expectedMin, expectedMax, MessageFormat.format("{0}..{1}",expectedMin, expectedMax));
    }

    /**
     * Creates a {@link ArgumentValidator} which checks for _expectedMin <= #arguments <= expectedMax_.
     *
     * @param expectedMin the minimum number of expected arguments
     * @param expectedMax the maximum number of expected arguments
     * @param description any description
     * @return an ArgumentValidator
     */
    public static ArgumentValidator inRange(int expectedMin, int expectedMax, String description) {
        if (expectedMin > 0)
            return allOf(
                    atLeast(expectedMin, description),
                    atMost(expectedMax, description)
            );

        return atMost(expectedMax, description);
    }

    /**
     * # Creates a {@link ArgumentValidator} which iterates over all `argumentValidators` until the first NOT {@link Type#VALID}
     * has been found.
     *
     * This is of course a logical AND.
     *
     * - If all are `VALID` then the result is also `VALID`.
     * - If at least one is *NOT VALID*, the first will be the result of the entire expression, but the error message will be replaced
     * with `description`
     *
     * @param argumentValidators 1 or more argument validators
     * @param description any description
     *
     * @return a AllOf validator
     */
    public static ArgumentValidator allOf(String description, ArgumentValidator... argumentValidators) {
        return new AllOf(Arrays.asList(argumentValidators), description);
    }

    /**
     * # Creates a {@link ArgumentValidator} which iterates over all `argumentValidators` until the first NOT {@link Type#VALID}
     * has been found.
     *
     * This is of course a logical AND.
     *
     * - If all are `VALID` then the result is also `VALID`.
     * - If at least one is *NOT VALID*, the first will be the result of the entire expression.
     *
     * @param argumentValidators 1 or more argument validators
     *
     * @return a AllOf validator
     */
    public static ArgumentValidator allOf(ArgumentValidator... argumentValidators) {
        return new AllOf(Arrays.asList(argumentValidators));
    }

    /**
     * # Creates a {@link ArgumentValidator} which iterates over all `argumentValidators` until the first {@link Type#VALID}
     * has been found.
     *
     * This is of course a logical OR.
     *
     * - If all are `VALID` then the result is also `VALID`.
     * - If at least one is `VALID`, then the entire expression is VALID.
     * - If all are *NOT VALID*, the result is the first invalid result, but the error message will be replaced
     * with `description`.
     *
     * @param argumentValidators 1 or more argument validators
     * @param description any description
     *
     * @return a AnyOf validator
     */
    public static ArgumentValidator anyOf(String description, ArgumentValidator... argumentValidators) {
        return new AnyOf(Arrays.asList(argumentValidators), description);
    }

    /**
     * # Creates a {@link ArgumentValidator} which iterates over all `argumentValidators` until the first {@link Type#VALID}
     * has been found.
     *
     * This is of course a logical OR.
     *
     * - If all are `VALID` then the result is also `VALID`.
     * - If at least one is `VALID`, then the entire expression is VALID.
     * - If all are *NOT VALID*, the result is the first invalid result.
     *
     * @param argumentValidators 1 or more argument validators
     *
     * @return a AnyOf validator
     */
    public static ArgumentValidator anyOf(ArgumentValidator... argumentValidators) {
        return new AnyOf(Arrays.asList(argumentValidators));
    }

    /**
     * # Creates a {@link ArgumentValidator} which applies the predicate on the entire argument list.
     *
     * @param errorDescription the error description in case that the predicate returns {@code false}.
     * @param argumentListPredicate the {@link Predicate} to be applied.
     *
     * @return an {@link ArgumentValidator} which uses an argument predicate.
     */
    public static ArgumentValidator argumentTypesValidator(String errorDescription, ArgumentListPredicate argumentListPredicate) {
        return new ArgumentTypesValidator(errorDescription, argumentListPredicate);
    }

    /**
     * # Creates a {@link ArgumentValidator} which apply the {@link ArgumentPredicate} on all arguments.
     *
     * This is convenient for
     * ```java
     *      {@linkplain #argumentTypeValidator(String, IndexFilter, ArgumentPredicate) argumentTypeValidator}(
     *              description,
     *              PredefinedIndexFilters.all(),
     *              argumentPredicate
     *      );
     * ```
     *
     * @param description the description in case that the predicate returns {@code false}.
     * @param argumentPredicate the {@link ArgumentPredicate} to be applied on a single argument.
     *
     * @return the {@link ArgumentValidator}
     *
     * @see IndexFilter#all()
     * @see PredefinedArgumentPredicates
     */
    public static ArgumentValidator argumentTypeValidator(String description, ArgumentPredicate argumentPredicate) {
        return argumentTypeValidator(description, IndexFilter.all(), argumentPredicate);
    }

    /**
     * # Creates a {@link ArgumentValidator} which apply the {@link ArgumentPredicate} on all arguments an the accepting {@code indexFilter}.
     *
     *
     * Example:
     * ```java
     *    // Creates a {@link ArgumentValidator} which check's if 2nd and 3rd argument has an integer value
     *    // in range 3 to 7
     *    argumentTypeValidator(
     *              "only 3..7 for 2nd and 3rd argument",
     *              {@linkplain IndexFilter#at(int...) at(1,2)},
     *              {@linkplain PredefinedArgumentPredicates#inRange(int, int) inRange(3,7)}
     *         );
     *
     * ```
     *
     * @param description the description in case that the predicate returns {@code false}.
     * @param indexFilter only on specified indices, should the {@code argumentPredicate} be applied.
     *
     * @param argumentPredicate the {@link ArgumentPredicate} to be applied on a single argument.
     * @return the {@link ArgumentValidator}
     *
     * @see IndexFilter
     * @see PredefinedArgumentPredicates
     */
    public static ArgumentValidator argumentTypeValidator(String description, IndexFilter indexFilter, ArgumentPredicate argumentPredicate) {
        return new EachArgumentTypeValidator(description, indexFilter, argumentPredicate);
    }


//-------------------------------------------
// Standard ArgumentValidator implementations
//-------------------------------------------

    private static class EachArgumentTypeValidator extends ArgumentValidatorBase {

        private final IndexFilter indexFilter;
        private final ArgumentPredicate argumentPredicate;

        private EachArgumentTypeValidator(String errorDescription, IndexFilter indexFilter, ArgumentPredicate argumentPredicate) {
            super(errorDescription);
            this.indexFilter = indexFilter;
            this.argumentPredicate = argumentPredicate;
        }

        @Override
        public ValidationResult validate(List<String> arguments) {
            int idx=0;
            for (String argument : arguments) {
                if( indexFilter.filter(idx) && ! argumentPredicate.test(argument) ) {
                    return typeMismatch(MessageFormat.format("{0}. Argument \"{1}\" not accepted!", this.description, argument));
                }
                idx += 1;
            }

            return VR_VALID;
        }
    }

    private static class ArgumentTypesValidator extends ArgumentValidatorBase {
        private final ArgumentListPredicate argumentPredicate;

        private ArgumentTypesValidator(String description, ArgumentListPredicate argumentPredicate) {
            super(description);
            this.argumentPredicate = argumentPredicate;
        }

        @Override
        public ValidationResult validate(List<String> arguments) {
            if( ! argumentPredicate.test(arguments) ) {
                return ValidationResult.typeMismatch(description);
            }

            return ArgumentValidator.VR_VALID;
        }
    }


    static final ValidationResult VR_INVALID_VALIDATOR = internalError("At least one validator must be set with anyOf() or allOf()." +
            "\nPlease contact the MarkdownTaglet's author!"
    );
    private static class AllOf extends ArgumentValidatorBase {
        private final List<ArgumentValidator> argumentValidators;

        private AllOf(List<ArgumentValidator> argumentValidators) {
            this.argumentValidators = argumentValidators;
        }

        private AllOf(List<ArgumentValidator> argumentValidators, String description) {
            super(description);
            this.argumentValidators = argumentValidators;
        }

        @Override
        public ValidationResult validate(List<String> arguments) {
            if( argumentValidators.isEmpty() )
                return VR_INVALID_VALIDATOR;

            for (ArgumentValidator argumentValidator : argumentValidators) {
                final ValidationResult result = argumentValidator.validate(arguments);
                if (result != ArgumentValidator.VR_VALID) {
                    return replaceErrorDescription(result);
                }
            }
            // All are VALID!
            return ArgumentValidator.VR_VALID;
        }
    }


    private static class AnyOf extends ArgumentValidatorBase {
        private final List<ArgumentValidator> argumentValidators;

        private AnyOf(List<ArgumentValidator> argumentValidators) {
            this.argumentValidators = argumentValidators;
        }

        private AnyOf(List<ArgumentValidator> argumentValidators, String description) {
            super(description);
            this.argumentValidators = argumentValidators;
        }

        @Override
        public ValidationResult validate(List<String> arguments) {
            if( argumentValidators.isEmpty() )
                return VR_INVALID_VALIDATOR;

            ValidationResult firstInvalid=null;
            for (ArgumentValidator argumentValidator : argumentValidators) {
                final ValidationResult result = argumentValidator.validate(arguments);
                if (result == ArgumentValidator.VR_VALID) {
                    return result;
                }

                if( firstInvalid==null )
                    firstInvalid = result;
            }

            // at least one must not be VALID!
            assert firstInvalid != null && firstInvalid.isNotValid() : "At least one must NOT VALID";

            return replaceErrorDescription(firstInvalid);
        }
    }

    private static class MissingArgumentsValidator extends ArgumentValidatorBase {
        private final int min;

        private MissingArgumentsValidator(int min, String description) {
            super(description);
            this.min = min;
        }

        @Override
        public ValidationResult validate(List<String> arguments) {
            if (arguments.size() < min) {
                return missingArguments(
                        MessageFormat.format("Expected {0} argument(s), got {1}!", this.description, arguments.size())
                );
            }
            return ArgumentValidator.VR_VALID;
        }
    }

    private static class TooManyArgumentsValidator extends ArgumentValidatorBase {
        private final int max;

        private TooManyArgumentsValidator(int max, String description) {
            super(description);
            this.max = max;
        }

        @Override
        public ValidationResult validate(List<String> arguments) {
            if (arguments.size() > max) {
                return tooManyArguments(
                         MessageFormat.format("Expected {0} argument(s), got {1}!", this.description, arguments.size())
                );
            }
            return ArgumentValidator.VR_VALID;
        }
    }

}
