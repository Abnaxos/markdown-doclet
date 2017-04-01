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
package mdtaglets

import ch.raffael.mddoclet.mdtaglet.MarkdownTaglet
import ch.raffael.mddoclet.mdtaglet.MarkdownTagletBase
import ch.raffael.mddoclet.mdtaglet.MarkdownTaglets
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import spock.lang.Specification
import spock.lang.Subject

import java.nio.file.Path

import static ch.raffael.mddoclet.mdtaglet.MarkdownTagletUtils.stripBlanksFromLineEnd
import static ch.raffael.mddoclet.mdtaglet.MarkdownTagletUtils.stripBlanksFromLineStart
/**
 * MarkdownTagletSpecBase should be used for testing {@link ch.raffael.mddoclet.mdtaglet.MarkdownTaglet}.
 */
@Subject([MarkdownTagletBase, MarkdownTaglets])
abstract class MarkdownTagletSpecBase extends Specification {
    private static final String CHARSET = 'UTF-8'
    private static final Path JAVADOC_TARGET_PATH = MarkdownTagletJavadocRunner.OUTPUT_PATH

    protected final javadocRunner = new MarkdownTagletJavadocRunner()

    void setup() {
        reset()
    }

    static void reset() {
        println("Reset javadoc runner")
        MarkdownTagletJavadocRunner.cleanTargetPath();

        println("Reset markdown taglets")
        MarkdownTaglets.reset();
    }

    /**
     * Create a markdown taglet option list.
     * @param markdownTagletClasses the markdown taglet classes.
     * @return the markdown taglet options
     */
    protected static List<String> markdownTaglets(Class<? extends MarkdownTaglet>... markdownTagletClasses) {
        def result = []
        for (def mdtc : markdownTagletClasses) {
            result += MarkdownTaglet.OPT_MD_TAGLET;
            result += mdtc.getName()
        }
        return result
    }

    /**
     * Resolve the the generated Javadoc.
     * @param clazz the class
     * @return Jsoup's document object
     */
    protected static Document resolveGeneratedHtmlJavadoc(Class clazz) {
        parse(clazz.getName())
    }


    private static Document parse(String path) {
        Jsoup.parse(
                JAVADOC_TARGET_PATH.resolve(path.replace('.', File.separator) + '.html').toFile(),
                CHARSET)
    }

    protected static String normalize(String html) {
        return stripBlanksFromLineEnd(stripBlanksFromLineStart(html))
    }
}
