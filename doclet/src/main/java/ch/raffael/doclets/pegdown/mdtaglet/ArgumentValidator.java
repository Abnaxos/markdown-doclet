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

import ch.raffael.doclets.pegdown.mdtaglet.argval.ArgumentValidatorBase;
import ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentValidators;

import java.util.List;

/**
 * A {@link MarkdownTaglet} argument validator validates the arguments before {@link MarkdownTaglet#render(List)} will
 * be called.
 *
 * If the argument's are not valid {@link MarkdownTaglet#render(List)} must not be executed.
 *
 * It's possible to create your own, but it's not recommended. Use {@link PredefinedArgumentValidators} methods instead.
 *
 * @see MarkdownTaglet#getArgumentValidator()
 * @see PredefinedArgumentValidators
 * @see MarkdownTagletExecutor#apply(String) `
 */
public interface ArgumentValidator {

    /**
     * The VALID {@link ValidationResult}.
     */
    ValidationResult VR_VALID = ValidationResult.valid();

    /**
     * Too many arguments {@link ValidationResult}.
     */
    ValidationResult VR_TOO_MANY_ARGUMENTS = ValidationResult.tooManyArguments("");

    /**
     * Missing arguments {@link ValidationResult}.
     */
    ValidationResult VR_MISSING_ARGUMENTS = ValidationResult.missingArguments("");

    /**
     * Type mismatch {@link ValidationResult}.
     */
    ValidationResult VR_TYPE_MISMATCH = ValidationResult.typeMismatch("");

    /**
     * Internal Error {@link ValidationResult}.
     */
    ValidationResult VR_INTERNAL_ERROR = ValidationResult.internalError("");

    /**
     * # Validates the tags argument list.
     *
     * @param arguments the argument list
     *
     * @return A {@link ValidationResult} instance
     *
     * @see MarkdownTaglet#render(List)
     * @see #VR_VALID
     */
    ValidationResult validate(List<String> arguments);


    /**
     * # A Usage hint!
     *
     * Please do not implement this interface: Extend from {@link ArgumentValidatorBase} or better
     * use the {@link PredefinedArgumentValidators}.
     *
     * @see PredefinedArgumentValidators
     * @see ArgumentValidatorBase
     */
    void __extend_ArgumentValidatorBase_instead_of_implementing_this_interface();
}
