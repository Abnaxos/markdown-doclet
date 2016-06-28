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

import ch.raffael.doclets.pegdown.mdtaglet.argval.PredefinedArgumentValidators;

import java.util.List;

/**
 * # MarkdownTagletBase is the default base class implementation for {@link MarkdownTaglet}.
 *
 * Every markdown taglet should be extend this base class. It provide some default implementation and protect your
 * taglet for further interface changes.
 */
public abstract class MarkdownTagletBase implements MarkdownTaglet {

    /**
     * Default implementation.
     *
     * @return empty string (no description).
     */
    @Override
    public String getDescription() {
        return "";
    }


    @Override
    public void afterOptionsSet() throws Exception {
        // do nothing.
    }

    /**
     * Default implementation.
     *
     * @return {@link PredefinedWhiteSpacePreserver#KEEP_ALL}
     */
    @Override
    public WhiteSpacePreserver getWhiteSpacePreserver() {
        return PredefinedWhiteSpacePreserver.KEEP_ALL;
    }

    /**
     * Default implementation returns {@code true}.
     *
     * *Remark*: If you want to use {@link #renderRaw(String)} you have to override this method and return {@code false}.
     *
     * The origin documentation:
     *
     * {@inheritDoc}
     *
     * @return {@code true}.
     */
    @Override
    public boolean useArgumentValidator() {
         return true;
    }

    /**
     * Default implementation.
     *
     * @return {@link PredefinedArgumentValidators#ZERO_OR_MORE}
     *
     */
    @Override
    public ArgumentValidator getArgumentValidator() {
        return PredefinedArgumentValidators.ZERO_OR_MORE;
    }

    /**
     * Default implementation, will always throw an exception.
     *
     * *Remarks*:
     *
     * + There is no need to override it, until {@link #useArgumentValidator()} returns {@code false}.
     * + Just implement {@link #render(List)}.
     *
     * @param tagContent the tag's content
     *
     * @return nothing
     *
     * @throws UnsupportedOperationException must be overridden.
     *
     * @see #useArgumentValidator()
     */
    @Override
    public String renderRaw(String tagContent) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("renderRaw() must not be called");
    }


    @Override
    public final void __dont_implement_MarkdownTaglet__extend_MarkdownTagletBase() {
        throw new UnsupportedOperationException("Not called.");
    }


    protected static String emptyLines(int numberOfEmptyLines) {
        final String lineSeparator = System.lineSeparator();
        String lines="";

        for (int i = 0; i <= numberOfEmptyLines; i++) {
            lines += lineSeparator;
        }

        return lines;
    }

    protected static String newline() {
        return emptyLines(0);
    }


}
