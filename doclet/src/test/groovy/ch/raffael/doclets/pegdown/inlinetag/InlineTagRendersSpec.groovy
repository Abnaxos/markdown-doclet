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
package ch.raffael.doclets.pegdown.inlinetag

import ch.raffael.doclets.pegdown.Options
import spock.lang.Specification
import spock.lang.Unroll

/**
 * InlineTagRendersSpec contains specification for ... .
 */
class InlineTagRendersSpec extends Specification {

    @Unroll
    def "What happens applying standard renders on '#markup'?"() {
        given: "standard render"
        def render = InlineTagRenders.standardRenders(new Options())

        when: "apply render on #markup"
        def markdown = render.render(markup)

        then: "should result in expected markdown"
        markdown == expected

        where:
        markup                                     || expected
        ""                                         || ""
        "any text"                                 || "any text"
        "{@gist feafcf888d949627001948b8346e0da7}" || validGist()
        "{@gist invalid-gist-id}"                  || invalidGist('invalid-gist-id')
        "{@gist }"                                 || invalidGist('')
        "{@gist}"                                  || "{@gist}"
    }

    static validGist() {
        return """
                  |```java
                  |/**
                  | * Java doc
                  | */
                  |@AnyAnnotation
                  |public class MyClass {
                  |  // Any method
                  |}
                  |```
                  |[Gist on Github](https://gist.github.com/feafcf888d949627001948b8346e0da7) and [Raw File GistTest.java](https://gist.githubusercontent.com/loddar/feafcf888d949627001948b8346e0da7/raw/f1f351b465132dc935fe46253cf5249de2e0935c/GistTest.java)
                  """.stripMargin().trim()
    }

    static invalidGist(String gistid) {
        return """
                  |```
                  |No gist found for gistid '${gistid}'
                  |```
                  |[Gist on Github](https://api.github.com/gists/${gistid}) and [Raw File unknown](https://api.github.com/gists/${gistid})
               """.stripMargin().trim()
    }
}