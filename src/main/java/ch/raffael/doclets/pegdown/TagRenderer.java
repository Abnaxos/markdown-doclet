/*
 * Copyright 2013 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
