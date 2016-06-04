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

import groovy.json.JsonSlurper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * GistClient is responsible for loading the gists from Github using Github's REST API.
 */
final class GistRestClient implements GistClient {

    private static final String HTTPS_API_GITHUB_COM_GISTS = "https://api.github.com/gists/";
    private static final GistRestClient STANDARD_GIST_CLIENT = new GistRestClient();

    private final JsonSlurper jsonSlurper = new JsonSlurper();
    private final URL url;
    private final String gistId;

    private GistRestClient() {
        this((URL) null, "no-gist");
    }

    /**
     * Package private for testing purposes.
     *
     * @param url    the URL
     * @param gistId the gistid
     */
    GistRestClient(URL url, String gistId) {
        this.url = url;
        this.gistId = gistId;
    }

    static GistClient standardGistClient() {
        return STANDARD_GIST_CLIENT;
    }

    private static URL createGithubUrl(String gistId) throws MalformedURLException {
        return new URL(toUrlString(gistId));
    }

    private static String toUrlString(String gistId) {
        return HTTPS_API_GITHUB_COM_GISTS + gistId.trim();
    }

    List<GistItem> resolveGists() {
        if (url == null) {
            throw new IllegalStateException("Please use resolveGists(<gistid>)");
        }
        try {
            return this.doResolveGistItemsFromURL();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace(System.err);
            return Collections.singletonList(GistItem.createErrorGistItem(gistId, toUrlString(gistId), "Parse error: " + e.getMessage()));
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.singletonList(GistItem.createErrorGistItem(gistId, toUrlString(gistId), "No gist found for gistid '" + gistId + "'"));
        }
    }


    @Override
    public List<GistItem> resolveGists(String gistId) {
        try {
            return new GistRestClient(createGithubUrl(gistId), gistId).resolveGists();
        } catch (Exception e) {
            throw new RuntimeException("Caught unexpected exception", e);
        }
    }

    private List<GistItem> doResolveGistItemsFromURL() throws Exception {
        final Map<String, ?> response = getRootJsonObject(jsonSlurper.parse(url, "UTF-8"), Object.class);
        final String id = getStringValue(response, "id");
        final String htmlUrl = getStringValue(response, "html_url");
        final Map<String, ?> files = getJsonObject(response, "files", Object.class);

        return toGistItems(id, htmlUrl, files);
    }

    private List<GistItem> toGistItems(String id, String htmlUrl, Map<String, ?> files) {
        final List<GistItem> gists = new LinkedList<>();
        for (Object file : files.values()) {
            @SuppressWarnings("unchecked")
            final Map<String, ?> gist = typeSafe(file,"files",Map.class);

            final String language = getStringValue(gist, "language");
            final String filename = getStringValue(gist, "filename");
            final String raw_url = getStringValue(gist, "raw_url");
            final String content = getStringValue(gist, "content");

            gists.add(new GistItem(id, htmlUrl, filename, language, raw_url, content));
        }

        return gists;
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String,?> getRootJsonObject(Object input, Class<T> clazz) {
        return typeSafe(input, "root", Map.class);
    }

    private String getStringValue(Map<String, ?> jsonObject, String field) {
        final Object value = getNullSafetyObject(jsonObject, field);
        return typeSafe(value, field, String.class);

    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T>  getJsonObject(Map<String, ?> jsonObject, String field, Class<T> clazz) {
        final Object value = getNullSafetyObject(jsonObject, field);

        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Field '" + field + "' is not of type Map (a JSON object). " + githubChangeRestAPI());
        }

        return typeSafe(value, field, Map.class);

    }

    private Object getNullSafetyObject(Map<String, ?> jsonObject, String field) {
        final Object value = jsonObject.get(field);
        if( value==null ) {
            throw new IllegalArgumentException(
                    "Field '" + field + "' not found or value is null. " + githubChangeRestAPI()
            );
        }
        return value;
    }


    private <T> T typeSafe(Object value, String field, Class<T> type) {
        if (! type.isInstance(value)) {
            throw new IllegalArgumentException(
                    "Field '" + field + "' is not of type " + type.getSimpleName()
                    + ". " + githubChangeRestAPI()
                );
        }
        return type.cast(value);
    }

    private String githubChangeRestAPI() {
        return "Github changes the for GIST-REST-API (using " + toUrlString(this.gistId) + ")";
    }
}
