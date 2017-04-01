/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
 *
 * This file is part of markdown-doclet.
 *
 * markdown-doclet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * markdown-doclet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with markdown-doclet.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package mdtaglets

import ch.raffael.mddoclet.mdt.gist.GistMarkdownTaglet
import com.google.common.io.Files
import spock.lang.Ignore
import spock.lang.Subject
import spock.lang.Unroll

@Subject(GistMarkdownTaglet)
@Unroll
class GistMarkdownTagletSpec extends MarkdownTagletSpecBase {

    def "How to use the gist markdown taglet? - #clazz"() {
        when: "Run javadoc for #clazz"
        def success = javadocRunner.generateJavadoc([clazz])

        and: "extract the javadoc"
        def javadoc = resolveGeneratedHtmlJavadoc(clazz)

        and: "select the html content"
        def html = javadoc.select('div.contentContainer div.description div.block').html()

        then: "javadoc has been successfully generated"
        success

        and: "the gist taglet has been applied"
        normalize(html) == expectedHtml(expected)

        where:
        clazz                       || expected
        UseGistTagSimple            || "use-gist-tag-simple.txt"
        UseGistTagMultipleFiles     || "use-gist-tag-multiple-files.txt"
        UseGistTagComplexExample    || "use-gist-tag-complex.txt"
        UseGistTagSelectFileExample || "use-gist-tag-select-file.txt"
        UseGistTagSecret            || "use-gist-tag-secret.txt"
    }

    @Unroll("How to #desc on gist markdown taglet?")
    def "What kind of rendering options are available?"() {
        given: "a class to render"
        def clazz = UseGistTagSimple

        when: "Run javadoc for #clazz"
        def success = javadocRunner.generateJavadoc([clazz], options)

        and: "extract the javadoc"
        def javadoc = resolveGeneratedHtmlJavadoc(clazz)

        and: "select the html content"
        def html = javadoc.select('div.contentContainer div.description div.block').html()

        then: "javadoc has been successfully generated"
        success

        and: "the gist taglet has been applied"
        normalize(html) == expectedHtml(expected)

        where:
        desc                               | options                            || expected
        "implicit enable gist description" | []                                 || "use-gist-tag-simple.txt"
        "explicit enable gist description" | ["-mdt-gist-description", "true"]  || "use-gist-tag-simple.txt"
        "disable gist description"         | ["-mdt-gist-description", "false"] || "use-gist-tag-simple-no-description.txt"
        "implicit enable blockquote"       | []                                 || "use-gist-tag-simple.txt"
        "explicit enable blockquote"       | ["-mdt-gist-indent", "true"]   || "use-gist-tag-simple.txt"
        "disable blockquote"               | ["-mdt-gist-indent", "false"]  || "use-gist-tag-simple-no-indent.txt"
    }


    def "How to overwrite general rendering options (-mdt-...) within a gist tag? - #clazz / #options"() {
        when: "Run javadoc for #clazz"
        def success = javadocRunner.generateJavadoc([clazz], options)

        and: "extract the javadoc"
        def javadoc = resolveGeneratedHtmlJavadoc(clazz)

        and: "select the html content"
        def html = javadoc.select('div.contentContainer div.description div.block').html()

        then: "javadoc has been successfully generated"
        success

        and: "the gist taglet has been applied"
        normalize(html) == expectedHtml(expected)


        where:
        clazz                            | options                                                             || expected
        UseGistTagWithoutGistDescription | ["-mdt-gist-description", "true"]                                   || "use-gist-tag-simple-no-description.txt"
        UseGistTagWithGistDescription | ["-mdt-gist-description", "false"]                                  || "use-gist-tag-simple.txt"
        UseGistTagSimpleNoIndent      | ["-mdt-gist-indent", "true"]                                    || "use-gist-tag-simple-no-indent.txt"
        UseGistTagSimpleIndent        | ["-mdt-gist-indent", "false"]                                   || "use-gist-tag-simple.txt"
        UseGistTagSimple              | ["-mdt-gist-indent", "false", "-mdt-gist-description", "false"] || "use-gist-tag-simple-no-indent-and-desc.txt"
        UseGistTag_indent_desc        | ["-mdt-gist-indent", "false", "-mdt-gist-description", "false"] || "use-gist-tag-simple.txt"
    }


    def "What happens, if user set an unknown/invalid option?"() {
        given: "a class to render"
        def clazz = UseGistTagSimpleUnknownOption

        when: "Run javadoc for #clazz"
        def success = javadocRunner.generateJavadoc([clazz])

        and: "extract the javadoc"
        def javadoc = resolveGeneratedHtmlJavadoc(clazz)

        and: "select the html content"
        def html = javadoc.select('div.contentContainer div.description div.block').html()

        then: "javadoc has been successfully generated"
        success

        and: "the markdown taglet framework marks the error"
        normalize(html) == expectedHtml("invalid-usage-gist-tag.txt")
    }

    @Ignore("It's difficult to test (so enable the tests and run it one by one and check it)")
    @SuppressWarnings("GroovyPointlessBoolean")
    @Unroll("What happens if set #desc? - #options")
    def "What kind of github API options are available?"() {
        given: "a class to render"
        def clazz = UseGistTagSimple

        when: "Run javadoc for #clazz"
        def generated = javadocRunner.generateJavadoc([clazz], options)

        then: "javadoc has been successfully generated"
        generated == expected

        where:
        desc                            | options                                                            || expected
        "valid credentials"             | ["-mdt-gist-github-properties", "./github.properties"]             || true
        "invalid credentials"           | ["-mdt-gist-github-properties", "./github-invalid.properties"]     || false
        "missing property file"         | ["-mdt-gist-github-properties", "./missing.properties"]            || false
        "explicit disable github cache" | ["-mdt-gist-github-use-cache", "false"]                            || true
        "implicit enable github cache"  | []                                                                 || true
        "explicit enable github cache"  | ["-mdt-gist-github-use-cache", "true"]                             || true
        "github cache size"             | ["-mdt-gist-github-cache-size", "1" /*MB*/]                        || true
        "github invalid cache size"     | ["-mdt-gist-github-cache-size", "-1" /*MB*/]                       || true
        "github cache dir"              | ["-mdt-gist-github-cache-dir", Files.createTempDir().absolutePath] || true
        "github invalid cache dir"      | ["-mdt-gist-github-cache-dir", "/any-invalid-dir"]                 || true
    }


    private static String expectedHtml(String resource) {
        return normalize(GistMarkdownTagletSpec.class.getResourceAsStream(resource)?.text ?: "!!! Error: ${resource} not found!");
    }
}
