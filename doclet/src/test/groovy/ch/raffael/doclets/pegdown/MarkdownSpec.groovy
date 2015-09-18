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
package ch.raffael.doclets.pegdown

import spock.lang.Specification

/**
 * @author <a href="mailto:slovdahl@hibox.fi">Sebastian Lövdahl</a>
 */
class MarkdownSpec extends Specification {

    private String result

    def "Plain string"() {
      when:
        result = render 'testing'

      then:
        result == '<p>testing</p>'
    }

    def "Emphasized markdown string"() {
      when:
        result = render '*testing*'

      then:
        result == '<p><em>testing</em></p>'
    }

    def "Simple markdown list"() {
      when:
        result = render '- list item 1\n' +
                        '- list item 2'

      then:
        result == '<ul>\n' +
                  '  <li>list item 1</li>\n' +
                  '  <li>list item 2</li>\n' +
                  '</ul>'
    }

    def "Simple markdown list with preceding text"() {
      when:
        result = render 'this is how it works:\n' +
                        '- list item 1\n' +
                        '- list item 2'

      then:
        result == '<p>this is how it works</p>' +
                  '<ul>\n' +
                  '  <li>list item 1</li>\n' +
                  '  <li>list item 2</li>\n' +
                  '</ul>'
    }

    private static String render(String javadoc, Options options = new Options()) {
        return options.toHtml(javadoc, true)
    }
}
