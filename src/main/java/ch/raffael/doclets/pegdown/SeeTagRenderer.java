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

    private final Pattern SIMPLE_LINK;
    private final Pattern FULL_LINK;
    private final Pattern WIKI_LINK;

    public SeeTagRenderer() {
        SIMPLE_LINK = Pattern.compile("([^<]*)<([^>]+)>");
        FULL_LINK = Pattern.compile("\\[([^)]+)\\]\\(([^]\\s]+)\\)");
        WIKI_LINK = Pattern.compile("\\[\\[\\s*([^]\\s]+)(\\s+[^]]*)?\\]\\]");
    }

    @Override
    public void render(SeeTag tag, StringBuilder target, PegdownDoclet doclet) {
        boolean wikiLink = false;
        if ( tag.text().startsWith("\"") && tag.text().endsWith("\"") && tag.text().length() > 1 ) {
            String text = tag.text().substring(1, tag.text().length() - 1).trim();
            Matcher matcher = SIMPLE_LINK.matcher(text);
            if ( !matcher.matches() ) {
                matcher = FULL_LINK.matcher(text);
                if ( !matcher.matches() ) {
                    if ( (doclet.getOptions().getPegdownExtensions() & Extensions.WIKILINKS) != 0 ) {
                        matcher = WIKI_LINK.matcher(text);
                        wikiLink = matcher.matches();
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
            int urlIndex, labelIndex;
            if (wikiLink) {
                urlIndex = 1;
                labelIndex = 2;
            } else {
                urlIndex = 2;
                labelIndex = 1;
            }
            FastEncoder.encode(matcher.group(urlIndex), target);
            target.append("\">");
            String label = matcher.group(labelIndex);
            if ( label != null ) {
                label = label.trim();
            }
            if ( label == null || label.isEmpty() ) {
                label = matcher.group(urlIndex);
            }
            FastEncoder.encode(label, target);
            target.append("</a>");
        }
        else {
            TagRenderer.VERBATIM.render(tag, target, doclet);
        }
    }
}
