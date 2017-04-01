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

import ch.raffael.mddoclet.mdtaglet.MarkdownTaglet
import ch.raffael.mddoclet.mdtaglet.MarkdownTagletErrorHandler
import ch.raffael.mddoclet.mdtaglet.ValidationResult
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.raffael.mddoclet.mdtaglet.ArgumentValidator.VR_VALID
import static ch.raffael.mddoclet.mdtaglet.ValidationResult.Type.VALID
import static ch.raffael.mddoclet.mdtaglet.argval.ValidationResultHelper.*


/**
 * ValidationResultSpec is responsible for ...
 */
@Unroll
@Subject(ValidationResult)
class ValidationResultSpec extends Specification {
    private static final ANY_DESCRIPTION = "Any error description"
    private static final ANY_NEW_DESCRIPTION = "new description"

    private static final VR_TOO_MANY_ARGUMENTS = ValidationResult.tooManyArguments(ANY_DESCRIPTION);
    private static final VR_MISSING_ARGUMENTS = ValidationResult.missingArguments(ANY_DESCRIPTION);
    private static final VR_TYPE_MISMATCH = ValidationResult.typeMismatch(ANY_DESCRIPTION);
    private static final VR_INTERNAL_ERROR = ValidationResult.internalError(ANY_DESCRIPTION);

    def "What should happen with any NONE VALID ValidationResult? - #vr"() {
        given: "a markdown taglet"
        def markdownTaglet = Stub(MarkdownTaglet)

        and: "a error handler"
        def errorHandler = Mock(MarkdownTagletErrorHandler)

        when: "applying error handler on validation result"
        vr.applyErrorHandler(markdownTaglet, errorHandler)

        then: "expect #numCalls calls on errorHandler"
        1 * errorHandler.invalidTagletArguments(markdownTaglet, expected)

        and: "nothing else should be called on errorHandler"
        0 * errorHandler._

        where:
        vr                    || expected
        VR_TOO_MANY_ARGUMENTS || errTooMany(ANY_DESCRIPTION)
        VR_MISSING_ARGUMENTS  || errMissing(ANY_DESCRIPTION)
        VR_TYPE_MISMATCH      || errTypeMismatch(ANY_DESCRIPTION)
        VR_INTERNAL_ERROR     || errInternal(ANY_DESCRIPTION)
    }

    def "How to change the error description of any none VALID? - #vr"() {
        expect: "the descriptions should differ"
        ANY_NEW_DESCRIPTION!=ANY_DESCRIPTION

        when: "replace the error description"
        def newVr = vr.replaceErrorDescription(ANY_NEW_DESCRIPTION)

        then: "should be different instances (immutable)"
        !newVr.is(vr)

        and: "the instances have the same type"
        newVr.type == vr.type

        and: "the error descriptions should differ"
        newVr.error != vr.error

        and: "the new error description should be set"
        newVr.error == errorMessage

        and: "it's never valid"
        vr.isNotValid()

        where:
        vr                    || errorMessage
        VR_TOO_MANY_ARGUMENTS || errTooMany(ANY_NEW_DESCRIPTION)
        VR_MISSING_ARGUMENTS  || errMissing(ANY_NEW_DESCRIPTION)
        VR_TYPE_MISMATCH      || errTypeMismatch(ANY_NEW_DESCRIPTION)
        VR_INTERNAL_ERROR     || errInternal(ANY_NEW_DESCRIPTION)
    }

    def "How does a VALID ValidationResult differ from NONE VALID?"() {
        given: "any markdown taglet (null is sufficient here)"
        def markdownTaglet = null

        and: "a error handler"
        def errorHandler = Mock(MarkdownTagletErrorHandler)


        when: "apply error handler"
        VR_VALID.applyErrorHandler(markdownTaglet, errorHandler)

        and: "or using replaceErrorDescription"
        ValidationResult valid=VR_VALID.replaceErrorDescription("any description")


        then: "the error handler should NEVER be called"
        0 * errorHandler._

        and: "there will no error message"
        valid.error == ""

        and: "is of course of type VALID"
        valid.type == VALID

        and: "it's valid"
        valid.isValid()

        and: "there must be only one VALID"
        valid.is(VR_VALID)
    }
}
