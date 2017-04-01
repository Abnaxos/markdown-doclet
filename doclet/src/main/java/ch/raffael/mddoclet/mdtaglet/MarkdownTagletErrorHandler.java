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

import java.util.List;

/**
 * Error and warning handler interface.
 */
public interface MarkdownTagletErrorHandler {
    /**
     * # Called in case setting an option on a {@link MarkdownTaglet} throws an exception.
     * @param markdownTaglet the markdown taglet
     * @param exception the exception
     */
    void optionsSetError(MarkdownTaglet markdownTaglet, Throwable exception);

    /**
     * # Called in case {@link MarkdownTaglet#afterOptionsSet()} throws an exception.
     * @param markdownTaglet the markdown taglet
     * @param exception the exception
     */
    void afterOptionsSetError(MarkdownTaglet markdownTaglet, Exception exception);

    /**
     * # Called in case overriding an already existing {@link MarkdownTaglet}.
     *
     * @param oldMarkdownTaglet the old markdown taglet
     * @param newMarkdownTaglet the new markdown taglet
     */
    void overrideMarkdownTaglet(MarkdownTaglet oldMarkdownTaglet, MarkdownTaglet newMarkdownTaglet);

    /**
     * # Called in case the tag is invalid.
     *
     * @param markdownTaglet the markdown taglet
     * @param errorDescription  the error description
     */
    void invalidTagletArguments(MarkdownTaglet markdownTaglet, String errorDescription);

    /**
     * # Called in case {@linkplain MarkdownTaglet#render(List) render()} throws an exception.
     * @param markdownTaglet the markdown taglet
     * @param tag the tag which causes the taglet to throw an exception
     * @param exception the exception itself
     */
    void caughtUnexpectedException(MarkdownTaglet markdownTaglet, String tag, Exception exception);
}
