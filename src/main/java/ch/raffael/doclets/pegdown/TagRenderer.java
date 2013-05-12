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

import com.sun.javadoc.Tag;


/**
 * An abstraction for rendering tags.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public interface TagRenderer<T extends Tag> {

    /**
     * A do-nothing renderer. It just renders the tag without any processing.
     */
    TagRenderer<Tag> VERBATIM = new TagRenderer<Tag>() {
        @Override
        public void render(Tag tag, StringBuilder target, PegdownDoclet doclet) {
            target.append(tag.name()).append(" ").append(tag.text());
        }
    };
    /**
     * A renderer that completely elides the tag.
     */
    TagRenderer<Tag> ELIDE = new TagRenderer<Tag>() {
        @Override
        public void render(Tag tag, StringBuilder target, PegdownDoclet doclet) {
            // do nothing
        }
    };

    /**
     * Render the tag to the given target {@link StringBuilder}.
     *
     * @param tag       The tag to render.
     * @param target    The target {@link StringBuilder}.
     * @param doclet    The doclet.
     */
    void render(T tag, StringBuilder target, PegdownDoclet doclet);

}
