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
package ch.raffael.doclets.pegdown.tags

import ch.raffael.doclets.pegdown.Options
import ch.raffael.doclets.pegdown.PegdownDoclet
import com.sun.javadoc.RootDoc
import com.sun.javadoc.SeeTag
import spock.lang.Specification


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
class SeeTagRendererSpec extends Specification {

    private String result

    def "Simple links"() {
      when:
        render '"<http://www.example.com/>"'
      then:
        result == '@see <a href="http://www.example.com/">http://www.example.com/</a>'
    }

    def "Simple links with label"() {
      when:
        render '" Example <http://www.example.com/>"'
      then:
        result == '@see <a href="http://www.example.com/">Example</a>'
    }

    def "Full links"() {
      when:
        render '"[Example](http://www.example.com/)"'
      then:
        result == '@see <a href="http://www.example.com/">Example</a>'
    }

    def "Wiki-style links"() {
      when:
        render '"[[http://www.example.com/]]"'
      then:
        result == '@see <a href="http://www.example.com/">http://www.example.com/</a>'
    }

    def "Wiki-style links with label"() {
      when:
        render '"[[http://www.example.com/ Example]]"'
      then:
        result == '@see <a href="http://www.example.com/">Example</a>'
    }

    def "No Wiki-style links when disabled in options"() {
      given:
        def options = new Options()
        options.pegdownExtensions = (options.pegdownExtensions & ~options.pegdownExtensions)

      when:
        render '"[[http://www.example.com/ Example]]"', options

      then:
        result == '@see "[[http://www.example.com/ Example]]"'
    }

    private String render(String text, Options options=new Options()) {
        return render(see(text), options)
    }

    private String render(SeeTag tag, Options options=new Options()) {
        def doclet = new PegdownDoclet(options, Stub(RootDoc))
        def buf = new StringBuilder()
        new SeeTagRenderer().render(tag, buf, doclet)
        result = buf.toString()
        return result
    }

    private SeeTag see(String text) {
        def tag = Mock(SeeTag)
        tag.name() >> "@see"
        tag.text() >> text
        return tag
    }

}
