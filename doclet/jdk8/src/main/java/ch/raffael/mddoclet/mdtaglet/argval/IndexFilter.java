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

import java.util.HashSet;
import java.util.Set;

/**
 * IndexFilter filters valid indices.
 *
 * @see PredefinedArgumentValidators#argumentTypeValidator(String, IndexFilter, ArgumentPredicate)
 */
public abstract class IndexFilter {

    private final String name;

    private IndexFilter(String name) {
        this.name = name;
    }

    /**
     * # Filters indices.
     * @param index the index value to check.
     * @return {@code true} if the index value could be used, otherwise {@code false}.
     */
    public abstract boolean filter(int index);

    @Override
    public String toString() {
        return name;
    }

    /**
     * # Creates a {@link IndexFilter} which {@linkplain IndexFilter#filter(int)} returns} {@code true} if called with one of the indices within the closed range [{@code minIndex}, {@code maxIndex}].
     *
     * *Caution*: {@code minIndex} <= {@code maxIndex} expected, otherwise an exception will thrown.
     *
     * Example:
     * ```java
     *      {@link IndexFilter} idxPred=PredefinedIndexFilters.range(1,3);
     *      idxPred.filter(1); // --> true
     *      idxPred.filter(2); // --> true
     *      idxPred.filter(3); // --> true
     *
     *      // but
     *      idxPred.filter(0); // --> false
     *      idxPred.filter(4); // --> false
     * ```
     *
     * @param minIndex min valid index
     * @param maxIndex max valid index
     *
     * @return an {@link IndexFilter}
     */
    public static IndexFilter range(final int minIndex, final int maxIndex) {
        assert minIndex<=maxIndex : "Invalid range. min <= max expected";
        return new RangeIndexFilter(minIndex, maxIndex, "range[" + minIndex + ".." + maxIndex + "]");
    }

    /**
     * # Creates a {@link IndexFilter} which {@linkplain IndexFilter#filter(int)} returns} {@code true} if called with index <= {@code maxIndex}.
     *
     * *Caution*: {@code maxIndex}>=0 expected, otherwise an exception will thrown.
     *
     *
     * Example:
     * ```java
     *      {@link IndexFilter} idxPred=PredefinedIndexFilters.max(3);
     *      idxPred.filter(0); // --> true
     *      idxPred.filter(1); // --> true
     *      idxPred.filter(2); // --> true
     *      idxPred.filter(3); // --> true
     *
     *      // but
     *      idxPred.filter(4); // --> false
     * ```
     *
     * @param maxIndex max valid index
     *
     * @return an {@link IndexFilter}
     */
    public static IndexFilter max(final int maxIndex) {
        assert 0<=maxIndex : "Invalid max. max>=0 expected";
        return new RangeIndexFilter(0, maxIndex, "max[" + maxIndex + "]");
    }

    /**
     * # Creates a {@link IndexFilter} which {@linkplain IndexFilter#filter(int)} returns} {@code true} if called with index >= {@code minIndex}.
     *
     * *Caution*: {@code minIndex}>=0 expected, otherwise an exception will thrown.
     *
     *
     * Example:
     * ```java
     *      {@link IndexFilter} idxPred=PredefinedIndexFilters.min(3);
     *      idxPred.filter(3); // --> true
     *      idxPred.filter(4); // --> true
     *
     *      // but
     *      idxPred.filter(0); // --> false
     *      idxPred.filter(1); // --> false
     *      idxPred.filter(2); // --> false
     * ```
     *
     * @param minIndex min valid index
     *
     * @return an {@link IndexFilter}
     */
    public static IndexFilter min(final int minIndex) {
        assert minIndex>=0 : "Invalid min. 0<= min";
        return new RangeIndexFilter(minIndex, Integer.MAX_VALUE, "min[" + minIndex + "]");
    }

    /**
     * # Creates a {@link IndexFilter} which always returns {@code true} if called with an valid index value.
     *
     * This IndexFilter is useful, if you want to specify a ArgumentPredicate which is applicable to all arguments.
     * For example: all arguments should be an integer>=100.
     *
     * *Remark*: Every index value >= 0 is valid.
     *
     * Example:
     * ```java
     *      {@link IndexFilter} idxPred=PredefinedIndexFilters.all();
     *      idxPred.filter(0); // --> true
     *      idxPred.filter(4); // --> true
     *      idxPred.filter({@link Integer#MAX_VALUE}); // --> true
     *
     *      // but
     *      idxPred.filter(-1); // --> false
     *      idxPred.filter({@link Integer#MIN_VALUE}); // --> false
     * ```
     *
     *
     * @return an {@link IndexFilter}
     */
    public static IndexFilter all() {
        return new RangeIndexFilter(0, Integer.MAX_VALUE, "all");
    }

    /**
     * Creates a {@link IndexFilter} which {@linkplain IndexFilter#filter(int)} applies} to {@code true} if called
     * with one of the indices.
     *
     * *Remark*: The order of the indices is not important:
     *
     * ```
     *      at(0, 7).filter(0) == at(7, 0).filter(0)
     *      at(0, 7).filter(1) == at(7, 0).filter(1)
     *      ...
     *      at(0, 7).filter(7) == at(7, 0).filter(7)
     * ```
     *
     * Example:
     * ```java
     *      {@link IndexFilter} idxPred=PredefinedIndexFilters.at(0, 7);
     *      idxPred.filter(0); // --> true
     *      idxPred.filter(7); // --> true
     *
     *      // but
     *      idxPred.filter(1); // --> false
     * ```
     * @param validIndices the list of valid indices
     *
     * @return an {@link IndexFilter}
     */
    public static IndexFilter at(final int... validIndices) {
        return new IndexListFilter(validIndices);
    }

    private static class RangeIndexFilter extends IndexFilter {
        private final int minIndex;
        private final int maxIndex;

        private RangeIndexFilter(int minIndex, int maxIndex, String name) {
            super(name);
            this.minIndex = minIndex;
            this.maxIndex = maxIndex;
        }

        @Override
        public boolean filter(int index) {
            return index >= minIndex && index <= maxIndex;
        }
    }

    private static class IndexListFilter extends IndexFilter {
        private final Set<Integer> validIndices=new HashSet<>();

        private IndexListFilter(int[] validIndices) {
            super("at");
            for (int validIndex : validIndices) {
                this.validIndices.add(validIndex);
            }
        }

        @Override
        public boolean filter(int index) {
            return validIndices.contains(index);
        }

        @Override
        public String toString() {
            return super.toString() + this.validIndices;
        }
    }
}
