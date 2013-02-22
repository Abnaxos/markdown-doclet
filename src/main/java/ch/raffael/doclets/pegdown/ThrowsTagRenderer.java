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

import com.sun.javadoc.ThrowsTag;

import static ch.raffael.doclets.pegdown.TagRendering.*;


/**
 * Renderer for `@throws` and `@exception` tags.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class ThrowsTagRenderer implements TagRenderer<ThrowsTag> {

    public static final ThrowsTagRenderer INSTANCE = new ThrowsTagRenderer();

    @Override
    public void render(ThrowsTag tag, StringBuilder target, PegdownDoclet doclet) {
        target.append(tag.name())
                .append(' ').append(tag.exceptionName())
                .append(' ').append(simplifySingleParagraph(doclet.toHtml(tag.exceptionComment())));
    }
}
