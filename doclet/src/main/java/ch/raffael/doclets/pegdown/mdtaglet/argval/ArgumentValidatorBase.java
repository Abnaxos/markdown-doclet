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

import ch.raffael.doclets.pegdown.mdtaglet.ArgumentValidator;
import ch.raffael.doclets.pegdown.mdtaglet.ValidationResult;

/**
 * ArgumentValidatorBase is responsible for ...
 */
@SuppressWarnings("WeakerAccess")
public abstract class ArgumentValidatorBase implements ArgumentValidator {

    protected final String description;

    protected ArgumentValidatorBase(String description) {
        this.description = description;
    }

    protected ArgumentValidatorBase() {
        this("");
    }

    @Override
    public final String toString() {
        if( description.isEmpty() )
            return this.getClass().getSimpleName();
        return description;
    }

    final ValidationResult replaceErrorDescription(ValidationResult vr) {
        if( this.description.isEmpty() ) {
            return vr;
        }

        return vr.replaceErrorDescription(description);
    }

    @Override
    public final void __extend_ArgumentValidatorBase_instead_of_implementing_this_interface() {
        throw new UnsupportedOperationException("it's only an usage hint.");
    }
}
