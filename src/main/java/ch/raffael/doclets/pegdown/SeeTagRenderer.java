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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.SeeTag;
import org.pegdown.Extensions;
import org.pegdown.FastEncoder;


/**
 * Renderer for `@see` tags.
 *
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class SeeTagRenderer implements TagRenderer<SeeTag> {

    public static final SeeTagRenderer INSTANCE = new SeeTagRenderer();

    private static final Pattern SIMPLE_LINK = Pattern.compile("(?<label>[^<]*)<(?<url>[^>]+)>");
    private static final Pattern FULL_LINK = Pattern.compile("\\[(?<label>[^)]+)\\]\\((?<url>[^]\\s]+)\\)");
    private static final Pattern WIKI_LINK = Pattern.compile("\\[\\[\\s*(?<url>[^]\\s]+)(?<label>\\s+[^]]*)?\\]\\]");

    @Override
    public void render(SeeTag tag, StringBuilder target, PegdownDoclet doclet) {
        if ( tag.text().startsWith("\"") && tag.text().endsWith("\"") && tag.text().length() > 1 ) {
            String text = tag.text().substring(1, tag.text().length() - 1).trim();
            Matcher matcher = SIMPLE_LINK.matcher(text);
            if ( !matcher.matches() ) {
                matcher = FULL_LINK.matcher(text);
                if ( !matcher.matches() ) {
                    if ( (doclet.getOptions().getPegdownExtensions() & Extensions.WIKILINKS) != 0 ) {
                        matcher = WIKI_LINK.matcher(text);
                        if ( !matcher.matches() ) {
                            VERBATIM.render(tag, target, doclet);
                            return;
                        }
                    }
                    else {
                        VERBATIM.render(tag, target, doclet);
                        return;
                    }
                }
            }
            target.append(tag.name()).append(' ').append("<a href=\"");
            FastEncoder.encode(matcher.group("url"), target);
            target.append("\">");
            String label = matcher.group("label");
            if ( label != null ) {
                label = label.trim();
            }
            if ( label == null || label.isEmpty() ) {
                label = matcher.group("url");
            }
            FastEncoder.encode(label, target);
            target.append("</a>");
        }
        else {
            TagRenderer.VERBATIM.render(tag, target, doclet);
        }
    }
}
