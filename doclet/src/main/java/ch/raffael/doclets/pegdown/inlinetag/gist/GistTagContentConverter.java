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

import ch.raffael.doclets.pegdown.inlinetag.TagContentConverter;

import java.util.List;

/**
 * GistTagContentConverter is responsible for ...
 */
final class GistTagContentConverter implements TagContentConverter {

    private final GistClient gistClient;

    GistTagContentConverter(GistClient gistClient) {
        this.gistClient = gistClient;
    }

    @Override
    public String markdown(String gistid) {
        final StringBuilder result=new StringBuilder();
        final List<GistItem> gists = gistClient.resolveGists(gistid);
        boolean first=true;
        for (GistItem gist : gists) {
            if( ! first )
                result.append("\n\n");
            result.append(toMarkdown(gist));
            first=false;
        }
        return result.toString();
    }

    private static String toMarkdown(GistItem gist) {
        return "```" + gist.language.toLowerCase() + "\n" +
                gist.content + "\n" +
                "```" + "\n" +
                "[Gist on Github](" + gist.htmlUrl + ")" +
                " and " +
                "[Raw File " + gist.filename + "](" + gist.rawUrl + ")";
    }
}
