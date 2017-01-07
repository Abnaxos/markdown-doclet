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

package ch.raffael.doclets.pegdown.mdtaglet;

import java.util.Objects;

/**
 * ValidationResult is responsible for ...
 */
@SuppressWarnings("WeakerAccess")
public abstract class ValidationResult {
    private static final ValidationResult VALID = new ValidationResult(Type.VALID) {
        @Override
        public ValidationResult replaceErrorDescription(String error) {
            return this;
        }

        @Override
        void applyErrorHandler(MarkdownTaglet markdownTaglet, MarkdownTagletErrorHandler errorHandler) {
        }

        @Override
        public boolean isValid() {
            return true;
        }
    };

    private final Type type;
    private final String error;

    private ValidationResult(Type type) {
        this(type, "");
    }

    private ValidationResult(Type type, String error) {
        this.type = type;
        this.error = type.getErrorPrefix() + error;
    }

    /**
     * Factory method for {@link Type#MISSING_ARGUMENTS}.
     *
     * @param error the error message.
     *
     * @return a {@code ValidationResult}
     */
    public static ValidationResult missingArguments(String error) {
        return new Invalid(Type.MISSING_ARGUMENTS, error);
    }

    /**
     * Factory method for {@link Type#TOO_MANY_ARGUMENTS}.
     *
     * @param error the error message.
     *
     * @return a {@code ValidationResult}
     */
    public static ValidationResult tooManyArguments(String error) {
        return new Invalid(Type.TOO_MANY_ARGUMENTS, error);
    }

    /**
     * Factory method for {@link Type#TYPE_MISMATCH}.
     *
     * @param error the error message.
     *
     * @return a {@code ValidationResult}
     */
    public static ValidationResult typeMismatch(String error) {
        return new Invalid(Type.TYPE_MISMATCH, error);
    }

    /**
     * Factory method for {@link Type#INTERNAL_ERROR}.
     *
     * @param error the error message.
     *
     * @return a {@code ValidationResult}
     */
    public static ValidationResult internalError(String error) {
        return new Invalid(Type.INTERNAL_ERROR, error);
    }

    /**
     * Factory method for {@link Type#VALID}.
     *
     * @return THE VALID ValidationResult
     */
    public static ValidationResult valid() {
        return VALID;
    }


    public final Type getType() {
        return type;
    }

    public final String getError() {
        return error;
    }

    public abstract boolean isValid();

    public final boolean isNotValid() {
        return ! isValid();
    }


    public ValidationResult replaceErrorDescription(String error) {
        return new Invalid(type, error);
    }

    void applyErrorHandler(MarkdownTaglet markdownTaglet, MarkdownTagletErrorHandler errorHandler) {
        type.applyOnErrorHandler(errorHandler, markdownTaglet, this.error);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ValidationResult)) return false;
        ValidationResult that = (ValidationResult) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return type.toString();
    }


    public enum Type {
        VALID(""),
        MISSING_ARGUMENTS("Missing Argument(s): "),
        TOO_MANY_ARGUMENTS("Too Many Argument(s): "),
        TYPE_MISMATCH("Type mismatch: "),
        INTERNAL_ERROR("Internal Error: ");

        private final String errorPrefix;

        Type(String errorPrefix) {
            this.errorPrefix = errorPrefix;
        }

        void applyOnErrorHandler(MarkdownTagletErrorHandler errorHandler, MarkdownTaglet markdownTaglet, String errorError) {
            errorHandler.invalidTagletArguments(markdownTaglet, errorError);
        }


        String getErrorPrefix() {
            return errorPrefix;
        }
    }

    private static class Invalid extends ValidationResult {
        private Invalid(Type type, String error) {
            super(type, error);
        }

        @Override
        public boolean isValid() {
            return false;
        }
    }

}
