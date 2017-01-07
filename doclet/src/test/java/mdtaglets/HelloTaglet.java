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

package mdtaglets;

import ch.raffael.doclets.pegdown.mdtaglet.MarkdownTaglet;
import ch.raffael.doclets.pegdown.mdtaglet.MarkdownTagletBase;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * # HelloTaglet is a sample {@link MarkdownTaglet}.
 *
 * It's say Hello in English (language='EN') or Ciao in Italian (language='IT') to the people in the argument list.
 */
@SuppressWarnings("unused")
public final class HelloTaglet extends MarkdownTagletBase {

    private String language="EN";

    public HelloTaglet() {
    }

    private HelloTaglet(String language) {
        this.language = language;
    }

    @Override
    public String getName() {
        return "hello";
    }


    @Override
    public MarkdownTaglet createNewInstance() {
        return new HelloTaglet(this.language);
    }

    @Option("hello-lang")
    public void setLanguage(String language) {
        this.language = language;
    }


    @Override
    public String render(List<String> argumentList) throws Exception {
        return "_" + sayHelloTo(argumentList) + "_";
    }

    private String sayHelloTo(List<String> argumentList) {
        final String audience = " " + StringUtils.join(argumentList, ", ");
        switch(language) {
            case "IT":  return "Ciao" + audience;
        }
        return "Hello" + audience;
    }
}
