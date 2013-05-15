/*
 * Copyright 2013 Raffael Herzog
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
 */
package ch.raffael.doclets.pegdown;

import java.util.HashMap;
import java.util.Map;

import com.sun.javadoc.Doc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.Tag;


/**
 * A renderer for the newly introduced `@todo` tag.
 *
 * **Note:** This tag renderer is stateful and shouldn't be reused across several JavaDoc
 * runs.
 *
 * @todo This is just an example.
 *
 * You can use any content in here, like:
 *
 * ```java
 * public void sayHello() {
 *     System.out.println("Hello World");
 * }
 * ```
 *
 * @todo Several TODOs are possible.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class TodoTagRenderer implements TagRenderer<Tag> {

    public static final TodoTagRenderer INSTANCE = new TodoTagRenderer();

    private final Map<Doc, Counter> counters = new HashMap<>();

    /**
     * Render the tag.
     *
     * @todo TODOs can also be used in member docs ...
     *
     * @param tag       The tag to render.
     * @param target    The target {@link StringBuilder}.
     * @todo ... or even parameter docs -- which is a little strange, actually. ;)
     * @param doclet    The doclet.
     */
    @Override
    public void render(Tag tag, StringBuilder target, PegdownDoclet doclet) {
        Counter counter;
        if ( tag.holder() instanceof MemberDoc ) {
            counter = getCounter(((MemberDoc)tag.holder()).containingClass());
        }
        else {
            counter = getCounter(tag.holder());
        }
        target.append("<div class=\"todo\">");
        target.append("<div class=\"todoTitle\"><span class=\"todoTitle\">")
                .append(doclet.getOptions().getTodoTitle())
                .append("</span><span class=\"todoCounter\">#")
                .append(counter.next())
                .append("</span></div>");
        target.append("<div class=\"todoText\">");
        target.append(doclet.toHtml(tag.text().trim()));
        target.append("</div></div>");
    }

    private Counter getCounter(Doc doc) {
        Counter counter = counters.get(doc);
        if ( counter == null ) {
            counter = new Counter();
            counters.put(doc, counter);
        }
        return counter;
    }

    static final class Counter {
        private int counter = 1;
        int next() {
            return counter++;
        }
    }

}
