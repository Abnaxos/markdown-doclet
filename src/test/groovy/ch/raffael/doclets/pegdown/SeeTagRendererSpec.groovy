/*
 * Copyright 2013 Raffael Herzog
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.raffael.doclets.pegdown

import com.sun.javadoc.RootDoc
import com.sun.javadoc.SeeTag
import spock.lang.Specification;

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
