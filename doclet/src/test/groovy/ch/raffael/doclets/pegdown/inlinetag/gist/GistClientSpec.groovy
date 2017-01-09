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
package ch.raffael.doclets.pegdown.inlinetag.gist

import spock.lang.Specification
import spock.lang.Unroll
/**
 * GistClientSpec contains specification for ... .
 */
class GistClientSpec extends Specification {

    private static final String GIST_ID = '3de90e0ff4886ec145e8'

    private URL resolveGithubResponse(String resource) {
        this.getClass().getResource(resource)
    }

    def "What happens on valid gist ID?"() {
        given: "Gist client using a gist-example"
        def gistClient=new GistRestClient(resolveGithubResponse("gist-valid-example.json"), GIST_ID);

        when: "resolve the gists"
        def gistItems=gistClient.resolveGists()

        then: "should return expect gist items"
        gistItems==[expectedGistItem('GeofenceListenerImpl.java'), expectedGistItem('GeofenceSample.java')]
    }

    @Unroll
    def "What happens if Github changes JSON (#invalidJsonFile) - invalid field #field?"() {
        given: "Gist client using a invalid-gist-example"
        def gistid = GIST_ID
        def gistClient=new GistRestClient(resolveGithubResponse(invalidJsonFile), gistid);

        when: "resolve the gists"
        def gistItems=gistClient.resolveGists()

        then: "should be one error gist item"
        gistItems.size() == 1

        and: "appropriate error message"
        gistItems.each {
            assertErrorGistItem(
                    it,
                    gistid,
                    "Parse error: Field '${field}' not found or value is null. Github changes the for GIST-REST-API (using https://api.github.com/gists/${gistid})"
            )
        }

        where:
        field      | invalidJsonFile
        "id"       | "invalid-gist-example-id.json"
        "html_url" | "invalid-gist-example-html-url.json"
        "files"    | "invalid-gist-example-files.json"
        "filename" | "invalid-gist-example-filename.json"
        "language" | "invalid-gist-example-language.json"
        "raw_url"  | "invalid-gist-example-raw-url.json"
        "content"  | "invalid-gist-example-content.json"
    }

    // @Ignore("Disable if you have access to github and time ;-)")
    def "What happens to existing gist (using Gitgub)?"() {
        given: "Standard Gist client"
        GistClient gistClient=GistRestClient.standardGistClient()

        and: "valid GIST ID"
        def gistid = "feafcf888d949627001948b8346e0da7"

        when: "resolve the gists"
        def gistItems=gistClient.resolveGists(gistid)

        then: "should get expected gist item"
        gistItems==[expectedGistItem(gistid, 'GistTest.java')]
    }

    // @Ignore("Disable if you have access to github and time ;-)")
    def "What happens with invalid GIST ID (using Github)?"() {
        given: "Standard Gist client"
        GistClient gistClient=GistRestClient.standardGistClient()

        and: "invalid GIST ID"
        def gistid = "invalid-gist-id"

        when: "resolve the gists"
        def gistItems=gistClient.resolveGists(gistid)

        then: "should be exactly one (error) gist item"
        gistItems.size() == 1

        and: "which should contain"
        gistItems.each {
            assertErrorGistItem(it, gistid, "No gist found for gistid '${gistid}'")
        }
    }

    private static void assertErrorGistItem(GistItem gistItem, String gistid, String expectedErrorMessage) {
        assert gistItem.id == gistid
        assert gistItem.filename == 'unknown'
        assert gistItem.language == ''
        assert gistItem.rawUrl == "https://api.github.com/gists/${gistid}".toString()
        assert gistItem.htmlUrl == "https://api.github.com/gists/${gistid}".toString()
        assert gistItem.content == expectedErrorMessage
    }

    private static GistItem expectedGistItem(String filename) {
        return expectedGistItem(GIST_ID, filename)
    }

    private static GistItem expectedGistItem(String gistid, String filename) {
        return new GistItem(gistid, "don't care", filename, "don't care", "don't care", "don't care")
    }
}