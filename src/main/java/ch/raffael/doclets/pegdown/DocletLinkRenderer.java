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

import com.google.common.base.CharMatcher;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.WikiLinkNode;


/**
 * The default link renderer for this doclet. It overrides the rendering of Wiki-Style
 * links to support the following scheme:
 *
 * * `[[http://www.example.com/]]` is rendered as
 *   `<a href="http://www.example.com">www.example.com</a>`
 *
 * * `[[http://www.example.com/ Example]]` is rendered as
 *   `<a href="http://www.example.com">Example</a>`
 *
 * All other links will be rendered by Pegdown's default renderer.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class DocletLinkRenderer extends LinkRenderer {

    @Override
    public Rendering render(WikiLinkNode node) {
        String url = node.getText().trim();
        int pos = CharMatcher.WHITESPACE.indexIn(url);
        String text = url;
        if ( pos >= 0 ) {
            text = url.substring(pos).trim();
            url = url.substring(0, pos);
        }
        return new Rendering(url, text);
    }
}
