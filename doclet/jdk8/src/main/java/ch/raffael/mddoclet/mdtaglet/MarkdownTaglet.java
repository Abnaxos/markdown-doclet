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

package ch.raffael.mddoclet.mdtaglet;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

import ch.raffael.mddoclet.mdtaglet.argval.PredefinedArgumentValidators;

/**
 * # MarkdownTaglet represents a single markdown tag.
 *
 * A markdown tag has a
 *
 * + {@linkplain #getName() name}, which identifies your tag. _Caution_: case sensitive. 'Hello' or 'hello'
 *    are two different tags.
 * + the taglet's {@linkplain #getDescription() description}
 * + a {@linkplain #getWhiteSpacePreserver() whitespace preserver} strategy
 * + {@linkplain #getArgumentValidator() validates} the argument list
 * + the {@link #render(List)} or {@link #renderRaw(String)} method, which should create markdown code.
 *
 * Each markdown taglet **must** be registered in `META-INF/services/ch.raffael.mddoclet.mdtaglet.MarkdownTaglet`
 * (see {@link java.util.ServiceLoader}
 *
 * @see java.util.ServiceLoader
 */
public interface MarkdownTaglet {
    /**
     * Every taglet specific option must starts with {@value #OPT_MD_TAGLET_OPTION_PREFIX}.
     */
    String OPT_MD_TAGLET_OPTION_PREFIX = "-mdt-";

    /**
     * # The name of the tag (must be unique).
     *
     * @return the name
     */
    String getName();

    /**
     * # The MarkdownTaglet description.
     *
     * Will be used for error messages.
     *
     * @return the description
     */
    String getDescription();

    /**
     * # Called after the initialization of MarkdownTaglet is completed.
     *
     * Use this, if your taglet has any expensive, but shareable initialization tasks.
     *
     * @throws Exception any exception.
     *
     * @see ch.raffael.mddoclet.mdtaglet.MarkdownTaglet.Option
     */
    void afterOptionsSet() throws Exception;

    /**
     * # Create a new instance or itself.
     *
     * @return a MarkdownTaglet instance
     */
    MarkdownTaglet createNewInstance();

    /**
     * # Each MarkdownTaglet must define a {@link WhiteSpacePreserver}.
     *
     * A {@link WhiteSpacePreserver} is responsible what happens to the leading and trailing whitespaces around
     * the actually tag.
     *
     * @return the white space preserver
     *
     * @see PredefinedWhiteSpacePreserver
     */
    WhiteSpacePreserver getWhiteSpacePreserver();

    /**
     * # Defines which version of `render()` should be used.
     *
     * + returns {@code true}: Use {@link #render(List)} and {@link #getArgumentValidator()}.
     * + returns {@code false}: Use {@link #renderRaw(String)}.
     *
     * @return a boolean.
     */
    boolean useArgumentValidator();

    /**
     * # The argument validator, which validates the arguments.
     *
     * + {@link #render(List)} will be executed, only if the {@code argument list} will be accepted by the given {@link ArgumentValidator}
     * + You should use the helper class {@link PredefinedArgumentValidators}, too create a meaningful {@link ArgumentValidator}
     *
     * *Caution*: Will only be applied, if {@link #useArgumentValidator()} returns {@code true}.
     *
     * @return an ArgumentValidator.
     *
     * @see #useArgumentValidator()
     * @see PredefinedArgumentValidators
     * @see ArgumentValidator#validate(List)
     * @see ArgumentValidator#VR_VALID
     * @see #render(List)
     */
    ArgumentValidator getArgumentValidator();

    /**
     * # Render markdown using the tag's {@code argumentList}.
     *
     * *Caution*:
     *
     * + Will only be applied, if {@link #useArgumentValidator()} returns {@code true}.
     * + `render()` will by executed, only if the argument list is valid. This means
     *      {@linkplain #getArgumentValidator() the argument validator} returns a valid result.
     *
     *
     * @param argumentList the argument list
     *
     * @return the markdown (using the {@code argumentList})
     *
     * @throws Exception any exception while rendering
     *
     * @see #useArgumentValidator()
     * @see #getArgumentValidator()
     */
    String render(List<String> argumentList) throws Exception;

    /**
     * # Render markdown using the tag's (raw) content.
     *
     * Use this, if you need the entire content of the tag. Otherwise use {@link #render(List)}.
     *
     * *Caution*: Will be executed only if {@link #useArgumentValidator()} return {@code false}.
     *
     * @param tagContent the tagContent
     *
     * @return the markdown (using the tagContent)
     *
     * @throws Exception any exception while rendering
     *
     * @see #useArgumentValidator()
     * @see #render(List)
     */
    String renderRaw(String tagContent) throws Exception;

    /**
     * # Not called, just to express our wish not implement this interface.
     *
     * _Please extend your markdown taglet from {@link MarkdownTagletBase}._
     */
    @SuppressWarnings("unused")
    void __dont_implement_MarkdownTaglet__extend_MarkdownTagletBase();

    /**
     * # Annotate a public (taglet) method with one string parameter.
     *
     * Example:
     *
     * ```java
     * public class HelloTaglet extends {@link MarkdownTagletBase} {
     *     private String language="EN";
     *
     *     // Constructors ommitted
     *
     *    {@literal @Option}("hello-lang")
     *     public void setLanguage(String language) {
     *         this.language = language;
     *     }
     *
     *     // rest ommitted
     *
     * }
     * ```
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Documented
    @interface Option {
        /**
         * The option value without {@linkplain #OPT_MD_TAGLET_OPTION_PREFIX option prefix}.
         *
         * @return the option
         */
        String value();
    }
}
