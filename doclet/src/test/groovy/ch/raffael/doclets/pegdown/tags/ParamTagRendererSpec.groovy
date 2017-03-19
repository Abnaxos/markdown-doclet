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
import com.sun.javadoc.ParamTag
import com.sun.javadoc.RootDoc
import spock.lang.Specification


/**
 * @author Sebastian Lövdahl
 */
class ParamTagRendererSpec extends Specification {

    private String result

    def "Test normal parameter"() {
        when:
            result = render "String", "the string to check"
        then:
            result == "@param String the string to check"
    }

    def "Test single-letter generic type"() {
        when:
            result = render "<T>", "the type of the parameter"
        then:
            result == "@param <T> the type of the parameter"
    }

    def "Test multiple letter generic type"() {
        when:
        result = render "<TYPE>", "the type of the parameter"
        then:
        result == "@param <TYPE> the type of the parameter"
    }

    private String render(String type, String text, Options options=new Options()) {
        return render(param(type, text), options)
    }

    private String render(ParamTag tag, Options options=new Options()) {
        def doclet = new PegdownDoclet(options, Stub(RootDoc))
        def buf = new StringBuilder()
        new ParamTagRenderer().render(tag, buf, doclet)
        return buf.toString()
    }

    private ParamTag param(String type, String text) {
        def tag = Mock(ParamTag)
        tag.name() >> "@param"
        tag.parameterComment() >> text
        if (type.startsWith('<')) {
            tag.parameterName() >> type.substring(1, type.length() - 1)
            tag.isTypeParameter() >> true
        }
        else {
            tag.parameterName() >> type
            tag.isTypeParameter() >> false
        }
        return tag
    }
}
