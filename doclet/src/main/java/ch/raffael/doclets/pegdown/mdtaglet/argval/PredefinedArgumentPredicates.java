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

package ch.raffael.doclets.pegdown.mdtaglet.argval;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * # PredefinedArgumentPredicates provides predefined {@link ArgumentPredicate} factory methods.
 *
 * Some of regular expression based {@link ArgumentPredicate}
 *
 * + {@link #regex(String)}
 * + {@link #options(String...)} or {@link #options(List)}
 * + {@link #isInteger(Radix)}
 *
 * and some integer based
 *
 * + {@link #inRange(int, int)} and {@link #inRange(int, int, Radix)}
 * + {@link #min(int)}  and {@link #min(int, Radix)}
 * + {@link #max(int)}  and {@link #max(int, Radix)}
 */
public abstract class PredefinedArgumentPredicates {

    private static final ConcurrentMap<String, Pattern> patternCache = new ConcurrentHashMap<>();

    private PredefinedArgumentPredicates() {
        throw new UnsupportedOperationException("It's a utility class. Do not create an instance.");
    }

    private static Pattern createOrFetchPattern(String regex) {
        if (!patternCache.containsKey(regex)) {
            patternCache.putIfAbsent(regex, Pattern.compile(regex));
        }

        return patternCache.get(regex);
    }

    /**
     * # Creates an {@link ArgumentPredicate} which uses a regular expression (regex) as filter.
     *
     * @param regex the regular expression
     * @return the Regex {@link ArgumentPredicate}
     * @see java.util.regex.Pattern
     */
    public static ArgumentPredicate regex(String regex) {
        return new RegexArgumentPredicate(regex, createOrFetchPattern(regex));
    }

    /**
     * # Creates a {@link ArgumentPredicate} which checks, if one of the arguments is within the option array.
     *
     * *Remark*: Currently implemented by using {@link #regex(String)}. But don't rely on this implementation.
     *
     * @param options the options array
     * @return the {@link ArgumentPredicate}
     * @see #options(List)
     * @see #regex(String)
     */
    public static ArgumentPredicate options(String... options) {
        return options(Arrays.asList(options));
    }

    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is within the option list.
     *
     * @param optionList the option list
     * @return the {@link ArgumentPredicate}
     * @see #options(String...)
     * @see #regex(String)
     */
    public static ArgumentPredicate options(List<String> optionList) {
        return regex(StringUtils.join(new HashSet<>(optionList), "|"));
    }

    /**
     * # Creates a {@link ArgumentPredicate} which checks, if the argument is equal to the {@code stringValue}.
     *
     * *Remark*: Currently implemented by using {@link #regex(String)}. But don't rely on this implementation.
     *
     * @param stringValue a string value
     * @return the {@link ArgumentPredicate}
     * @see #regex(String)
     */
    public static ArgumentPredicate isEqual(String stringValue) {
        return regex(stringValue);
    }



    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is a positive or negative integer
     * in {@link Radix} notation.
     *
     * @param radix the radix
     * @return the {@link ArgumentPredicate}
     * @see #regex(String)
     */
    public static ArgumentPredicate isInteger(final Radix radix) {
        return regex(radix.regex);
    }


    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is a positive or negative integer
     * in {@link Radix#DECIMAL} notation.
     *
     * @param min the min value
     * @param max the max value
     * @return the {@link ArgumentPredicate}
     * @see Integer#parseInt(String, int)
     */
    public static ArgumentPredicate inRange(final int min, final int max) {
        return inRange(min, max, Radix.DECIMAL);
    }

    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is a positive or negative integer
     * in {@link Radix} notation.
     *
     * @param min   the min value
     * @param max   the max value
     * @param radix the radix
     * @return the {@link ArgumentPredicate}
     * @see Integer#parseInt(String, int)
     */
    public static ArgumentPredicate inRange(final int min, final int max, Radix radix) {
        return new IntegerPredicate(min, max, radix);
    }

    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is an integer >= {@code min}
     * in {@link Radix#DECIMAL} notation.
     *
     * @param min   the min value
     * @return the {@link ArgumentPredicate}
     * @see Integer#parseInt(String, int)
     */
    public static ArgumentPredicate min(int min) {
        return min(min, Radix.DECIMAL);
    }

    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is an integer >= {@code min}
     * in {@link Radix} notation.
     *
     * @param min   the min value
     * @param radix the radix
     * @return the {@link ArgumentPredicate}
     * @see Integer#parseInt(String, int)
     */
    public static ArgumentPredicate min(int min, Radix radix) {
        return inRange(min, Integer.MAX_VALUE, radix);
    }

    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is an integer <= {@code max}
     * in {@link Radix#DECIMAL} notation.
     *
     * @param max   the max value
     * @return the {@link ArgumentPredicate}
     *
     *
     * @see Integer#parseInt(String, int)
     */
    public static ArgumentPredicate max(int max) {
        return max(max, Radix.DECIMAL);
    }

    /**
     * # Creates an {@link ArgumentPredicate} which checks, if the argument is an integer <= {@code max}
     * in {@link Radix} notation.
     *
     * @param max   the max value
     * @param radix the radix
     * @return the {@link ArgumentPredicate}
     * @see Integer#parseInt(String, int)
     */
    public static ArgumentPredicate max(int max, Radix radix) {
        return inRange(Integer.MIN_VALUE, max, radix);
    }


    /**
     * Radix values for {@link #isInteger(Radix)} and Co.
     */
    public enum Radix {
        BINARY("[01]+", 2),
        OCTAL("[0-7]+", 8),
        DECIMAL("\\d+", 10),
        HEXADECIMAL("\\p{XDigit}+", 16);

        private final String regex;
        private final int radix;

        Radix(String regex, int radix) {
            this.regex = "[+-]?" + regex;
            this.radix = radix;
        }
    }

    private static class RegexArgumentPredicate extends ArgumentPredicate {
        private final Pattern pattern;

        RegexArgumentPredicate(String regex, Pattern pattern) {
            super("Regex('" + regex + "')");
            this.pattern = pattern;
        }

        @Override
        public boolean test(String argument) {
            return pattern.matcher(argument).matches();
        }
    }

    private static class IntegerPredicate extends ArgumentPredicate {
        private final int min;
        private final int max;
        private final Radix radix;

        private IntegerPredicate(int min, int max, Radix radix) {
            super("Integer(" + min + "," + max + "," + radix + ")");
            this.min = min;
            this.max = max;
            this.radix = radix;
        }

        @Override
        public boolean test(String argument) {
            try {
                final int value = Integer.parseInt(argument, radix.radix);
                return min <= value && value <= max;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
    }
}
