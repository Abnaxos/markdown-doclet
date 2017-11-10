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

package mdtaglets;

import java.util.List;

import com.google.common.base.Joiner;

import ch.raffael.mddoclet.mdtaglet.MarkdownTaglet;
import ch.raffael.mddoclet.mdtaglet.MarkdownTagletBase;

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
        final String audience = " " + Joiner.on(", ").join(argumentList);
        switch(language) {
            case "IT":  return "Ciao" + audience;
        }
        return "Hello" + audience;
    }
}
