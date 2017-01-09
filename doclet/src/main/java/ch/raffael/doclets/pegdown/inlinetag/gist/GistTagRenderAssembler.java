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

import ch.raffael.doclets.pegdown.Options;

/**
 * GistTagRenderAssembler is responsible for ...
 */
public final class GistTagRenderAssembler {
    
    public static GistTagRender createInlineTagRender(Options options) {
        final GistClient gistClient=GistRestClient.standardGistClient();
        final GistTagContentConverter markdownConverter = new GistTagContentConverter(gistClient);

        return new GistTagRender(markdownConverter);
    } 
}
