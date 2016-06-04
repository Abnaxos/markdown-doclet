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

import java.util.Objects;

/**
 * GistItem is responsible for ...
 */
final class GistItem {
    final String id;
    final String htmlUrl;
    final String filename;
    final String language;
    final String rawUrl;
    final String content;

    GistItem(String id, String htmlUrl, String filename, String language, String rawUrl, String content) {
        this.id = id;
        this.htmlUrl = htmlUrl;
        this.language = language;
        this.rawUrl = rawUrl;
        this.filename = filename;
        this.content = content;
    }

    static GistItem createErrorGistItem(String id, String url, String errorMessage) {
        return new GistItem(id, url, "unknown", "", url, errorMessage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GistItem)) return false;
        GistItem gistItem = (GistItem) o;
        return Objects.equals(id, gistItem.id) &&
                Objects.equals(filename, gistItem.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, filename, rawUrl);
    }


    @Override
    public String toString() {
        return "GistItem{" +
                "id='" + id + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", filename='" + filename + '\'' +
                ", rawUrl='" + rawUrl + '\'' +
                '}';
    }
}
