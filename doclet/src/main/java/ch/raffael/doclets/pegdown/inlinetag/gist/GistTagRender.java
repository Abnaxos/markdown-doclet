/*
 * Copyright 2013-2016 Raffael Herzog / Marko Umek
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

package ch.raffael.doclets.pegdown.inlinetag.gist;

import ch.raffael.doclets.pegdown.inlinetag.InlineTagRender;
import ch.raffael.doclets.pegdown.inlinetag.TagContentConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * GistTagRender is responsible for rendering the gist inline taglet.
 */
final class GistTagRender implements InlineTagRender {

    private static final Pattern GIST_PATTERN=Pattern.compile("\\s*\\{\\@gist\\s+(?<gistid>[^}]*)\\}\\s*");
    private static final Pattern EOL_PATTERN=Pattern.compile("\\s*+$");

    private TagContentConverter tagContentConverter;

    GistTagRender(TagContentConverter tagContentConverter) {
        this.tagContentConverter = tagContentConverter;
    }

    @Override
    public String render(String markup) {
        final Matcher matcher = GIST_PATTERN.matcher(markup);
        if( ! matcher.find() ) {
            return markup;
        }

        matcher.reset();
        return trimLines(replaceTags(matcher));
    }

    private String replaceTags(Matcher matcher) {
        final StringBuffer result = new StringBuffer();
        while(matcher.find()) {
            final String gistid=matcher.group("gistid");
            matcher.appendReplacement(result, resolveMarkdown(gistid));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String resolveMarkdown(String gistid) {
        return tagContentConverter.markdown(gistid);
    }

    private static String trimLines(String input) {
        return EOL_PATTERN.matcher(input).replaceAll("");
    }

}
