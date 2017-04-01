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

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ch.raffael.mddoclet.mdtaglet.argval.PredefinedArgumentValidators;

/**
 * # MarkdownTagletBase is the default base class implementation for {@link MarkdownTaglet}.
 *
 * Every markdown taglet should be extend this base class. It provide some default implementation and protect your
 * taglet for further interface changes.
 *
 * Remark: See the helper methods.
 *
 * - {@link #fetchMarkdownTagletDescriptionFile(String)}
 * - {@link #emptyLines(int)}
 * - {@link #newline()}
 * - {@link #htmlEmptyLines(int)}
 * - {@link #htmlNewline()}
 *
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class MarkdownTagletBase implements MarkdownTaglet {

    /**
     * Default implementation.
     *
     * @return empty string (no description).
     *
     * @see #fetchMarkdownTagletDescriptionFile(String)
     */
    @Override
    public String getDescription() {
        return "";
    }

    /**
     * Default implementation does nothing.
     * @throws Exception will never happen
     */
    @Override
    public void afterOptionsSet() throws Exception {
        // do nothing.
    }

    /**
     * Default implementation dos not create a new instance, it returns itself.
     * @return this
     */
    @Override
    public MarkdownTaglet createNewInstance() {
        return this;
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


    /**
     * Fetch the markdown taglet description from a resource within your classpath.
     * @param resourceName the resourceName
     * @return the content of the resource file or *empty string*.
     */
    protected String fetchMarkdownTagletDescriptionFile(String resourceName) {
        final URL url = this.getClass().getResource(resourceName);
        if( url!=null ) {
            try {
                return FileUtils.readFileToString(new File(url.toURI()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    /**
     * Create number of empty html lines (line breaks).
     * @param numberOfEmptyLines number of empty lines
     * @return #`\n<br>`
     */
    protected static String htmlEmptyLines(int numberOfEmptyLines) {
        final String lineSeparator = "\n<br>";
        return generateLineBreaks(numberOfEmptyLines, lineSeparator);
    }

    /**
     * A (HTML) line break.
     * @return a html line break.
     */
    protected static String htmlNewline() {
        return htmlEmptyLines(0);
    }

    /**
     * Create number of empty lines (line breaks).
     * @param numberOfEmptyLines number of empty lines
     * @return #`\n`
     */
    protected static String emptyLines(int numberOfEmptyLines) {
        final String lineSeparator = "\n";
        return generateLineBreaks(numberOfEmptyLines, lineSeparator);
    }

    /**
     * A line break.
     * @return a line break.
     */
    protected static String newline() {
        return emptyLines(0);
    }


    private static String generateLineBreaks(int numberOfEmptyLines, String lineSeparator) {
        String lines="";

        for (int i = 0; i <= numberOfEmptyLines; i++) {
            lines += lineSeparator;
        }

        return lines;
    }

}
