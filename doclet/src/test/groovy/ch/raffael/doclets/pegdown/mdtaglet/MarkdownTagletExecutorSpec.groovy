/*
 * Copyright 2013-2016 Raffael Herzog, Marko Umek
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
 *
 */


package ch.raffael.doclets.pegdown.mdtaglet

import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static ch.raffael.doclets.pegdown.mdtaglet.ArgumentValidator.*
import static ch.raffael.doclets.pegdown.mdtaglet.PredefinedWhiteSpacePreserver.*

@Unroll
class MarkdownTagletExecutorSpec extends Specification {

    @Subject
    private final markdownTagletExecutor = new MarkdownTagletExecutor()

    def "What should happen, if there is no registered MarkdownTaglet?"() {
        given: "markup with a tag"
        def text = "Any markup with a {{tag 12345 786}} tag"

        when: "apply markup"
        def markdown = markdownTagletExecutor.apply(text)

        then: "the markup must be unchanged"
        text == markdown
    }

    def "What are the accepted markdown pattern? - #markup"() {
        given: "a MarkdownTaglet"
        MarkdownTaglet anyTaglet = createMarkdownTagletStub("tag")

        when: "register markdown taglet"
        markdownTagletExecutor.register(anyTaglet)

        and: "apply #markup"
        def markdown = markdownTagletExecutor.apply(markup)

        then: "should render to the expected markdown"
        markdown == expected

        where:
        markup               || expected
        "{{tag  12345 786}}" || rendered("tag", "12345", "786")
        "{%tag  12345 786%}" || rendered("tag", "12345", "786")
        '{$tag  12345 786$}' || rendered("tag", "12345", "786")
    }

    def "What should happen to multiple registered MarkdownTaglets (#markup)?"() {
        given: "first MarkdownTaglet"
        MarkdownTaglet firstTaglet = createMarkdownTagletStub("first")

        and: "second MarkdownTaglet"
        def secondTaglet = createMarkdownTagletStub("second")

        when: "register markdown taglets"
        markdownTagletExecutor.register(firstTaglet).register(secondTaglet)

        and: "apply #markup"
        def markdown = markdownTagletExecutor.apply(markup)

        then: "should apply all registered MarkdownTaglets"
        markdown == expected

        where:
        markup                                   || expected
        "{{unknown 12345 786}}"                   || "{{unknown 12345 786}}"
        "{{first  12345 786}}"                    || rendered("first", "12345", "786")
        "{{second  12345 786}}"                   || rendered("second", "12345", "786")
        "{{first  12345}}{{second 786}}"           || rendered("first", "12345") + rendered("second", "786")
        "{{first  12345}}{{unknown}}{{second 786}}" || rendered("first", "12345") + "{{unknown}}" + rendered("second", "786")
    }

    def "How to evaluate the arguments? -  #markup"() {
        given: "a MarkdownTaglet named 'any'"
        MarkdownTaglet markdownTaglet = createMarkdownTagletStub("any")

        when: "register any markdown taglet"
        this.markdownTagletExecutor.register(markdownTaglet)

        and: "apply #markup"
        def markdown = this.markdownTagletExecutor.apply(markup)

        then: "the argument(s) should be evaluated"
        markdown == expected

        where:
        markup                              || expected
        "{{any}}"                            || rendered('any')
        "{{any }}"                           || rendered('any')
        "{{any    }}"                        || rendered('any')
        "{{any 12345 786}}"                  || rendered('any', '12345', '786')
        "{{any '12345' '786'}}"              || rendered('any', '12345', '786')
        "{{any '123 45' '786'}}"             || rendered('any', '123 45', '786')
        "{{any \"123 '45\" '7\"86' 988   }}" || rendered('any', '123 \'45', '7"86', '988')
        "{{any \"123'45 '786' \"988 }}"      || rendered('any', "123'45 '786' ", "988")
        '{{any \'123"45 "786" \'988 }}'      || rendered('any', '123"45 "786" ', "988")

        // What happens to new line within a tag
        "{{any one\ntwo}}"                   || rendered('any', 'one', 'two')
        "{{any\n1st line\n2nd line}}"        || rendered('any', '1st', 'line', '2nd', 'line')
    }

    def "What is the difference between the useArgumentValidator and raw version? - #markup, use argument validator=#useArgVal"() {
        given: "a MarkdownTaglet named 'any' and #useArgVal"
        MarkdownTaglet markdownTaglet = createMarkdownTagletStub("any", useArgVal)

        when: "register any markdown taglet"
        this.markdownTagletExecutor.register(markdownTaglet)

        and: "apply #markup"
        def markdown = this.markdownTagletExecutor.apply(markup)

        then: "the argument(s) should be evaluated"
        markdown == expected

        where:
        markup                       | useArgVal || expected
        "{{any 12345}}"               | true      || rendered('any', "12345")
        "{{any 12345}}"               | false     || rendered('any', "12345")
        "{{any '12345'}}"             | true      || rendered('any', "12345")
        "{{any '12345'}}"             | false     || rendered('any', "'12345'")
        "{{any '12345'     '786'  }}" | true      || rendered('any', "12345", "786")
        "{{any '12345'     '786'  }}" | false     || rendered('any', "'12345'     '786'  ")
        "{{any '12345'\n'786'}}"      | true      || rendered('any', "12345", "786")
        "{{any '12345'\n'786'}}"      | false     || rendered('any', "'12345'\n'786'")
    }

    def "What should happen to the leading/trailing whitespaces? - strategy #wsp"() {
        given: "an MarkdownTaglet (with given white space preserver)"
        MarkdownTaglet markdownTaglet = createMarkdownTagletStub("any", wsp)

        when: "register markdown taglet"
        this.markdownTagletExecutor.register(markdownTaglet)

        and: "apply #markup"
        def markdown = this.markdownTagletExecutor.apply(markup)

        then: "the argument(s) should be evaluated"
        markdown == expected

        where:
        wsp             | markup                                          || expected
        KEEP_ALL        | "Text before {{any 12345}} Text after"           || "Text before " + rendered('any', '12345') + " Text after"
        STRIP_ALL       | "Text before {{any 12345}} Text after"           || "Text before" + rendered('any', '12345') + "Text after"
        STRIP_NEW_LINES | "Text before\t\n\n{{any 12345}}\n\n\tText after" || "Text before\t" + rendered('any', '12345') + "\tText after"
        STRIP_NEW_LINES | "Text before\t\n\t\n{{any 12345}}\n\tText after" || "Text before\t\n\t" + rendered('any', '12345') + "\tText after"
        STRIP_BLANKS    | "Text before\n \t {{any 12345}} \t \nText after" || "Text before\n" + rendered('any', '12345') + "\nText after"
    }

    def "What happens when a MarkdownTaglet has the same name?"() {
        given: "first MarkdownTaglet with name 'tag'"
        MarkdownTaglet firstTaglet = createMarkdownTagletStub("tag", "first")

        and: "last MarkdownTaglet (with same name 'tag')"
        MarkdownTaglet lastTaglet = createMarkdownTagletStub("tag", "last")

        and: "set a error handler"
        def errorHandler = Mock(MarkdownTagletErrorHandler)
        markdownTagletExecutor.setErrorHandler(errorHandler)

        when: "register markdown taglets"
        markdownTagletExecutor.register(firstTaglet)
                .register(lastTaglet)

        and: "apply markup"
        def markdown = markdownTagletExecutor.apply("{{tag 123}}")


        then: "only the last registered MarkdownTaglet should be applied"
        markdown == rendered("last", "123")

        and: "overrideMarkdownTaglet() will be executed"
        1 * errorHandler.overrideMarkdownTaglet(_, _)
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    def "What happens with result of the MarkdownTaglet's argument validator? - #vr"() {
        given: "an argument validator returning #ar"
        def argVal = Stub(ArgumentValidator) {
            it.validate(_) >> vr.replaceErrorDescription("Invalid arguments!")
        }

        and: "a MarkdownTaglet (with argument validator)"
        MarkdownTaglet markdownTaglet = createMarkdownTagletStub("any", argVal)

        and: "set a error handler"
        def errorHandler = Mock(MarkdownTagletErrorHandler)
        markdownTagletExecutor.setErrorHandler(errorHandler)

        and: "register markdown taglet"
        this.markdownTagletExecutor.register(markdownTaglet)

        when: "apply #markup"
        def markdown = this.markdownTagletExecutor.apply("\n{{any arg0 arg1}}\n")

        then: "the markdown should be rendered to"
        markdown == expected

        and: "#calls to the error handler (with taglet and trimmed #expected)"
        calls * errorHandler.invalidTagletArguments(markdownTaglet, expected.trim())

        where:
        vr                    || calls | expected
        VR_VALID              || 0 | "\n" + rendered("any", "arg0", "arg1") + "\n"
        VR_MISSING_ARGUMENTS  || 1 | "\n{{any arg0 arg1}} << Missing Argument(s): Invalid arguments!\n"
        VR_TOO_MANY_ARGUMENTS || 1 | "\n{{any arg0 arg1}} << Too Many Argument(s): Invalid arguments!\n"
        VR_TYPE_MISMATCH      || 1 | "\n{{any arg0 arg1}} << Type mismatch: Invalid arguments!\n"
        VR_INTERNAL_ERROR     || 1 | "\n{{any arg0 arg1}} << Internal Error: Invalid arguments!\n"
    }

    def "What happens if a MarkdownTaglet throws a Exception? - #exception"() {
        given: "a MarkdownTaglet (which render method throwing #exception)"
        MarkdownTaglet markdownTaglet = createMarkdownTagletStub("any", exception)

        and: "set a error handler"
        def errorHandler = Mock(MarkdownTagletErrorHandler)
        markdownTagletExecutor.setErrorHandler(errorHandler)

        and: "register markdown taglet"
        this.markdownTagletExecutor.register(markdownTaglet)

        when: "apply #markup"
        def markdown = this.markdownTagletExecutor.apply("\n{{any arg0 arg1}}\n")

        then: "the markdown should be rendered to"
        markdown == "\n{{any arg0 arg1}} << " + exception.getClass().getName() + ": " + exception.getMessage() + "\n"

        and: "#calls to the error handler"
        1 * errorHandler.caughtUnexpectedException(markdownTaglet, "{{any arg0 arg1}}", exception)

        where:
        exception                                           || makeSpockHappy
        new IllegalArgumentException("a runtime exception") || _
        new IOException("a exception")                      || _
    }

    private MarkdownTaglet createMarkdownTagletStub(String tagName) {
        createMarkdownTagletStub(tagName, true)
    }

    private MarkdownTaglet createMarkdownTagletStub(String tagName, boolean useArgumentValidator) {
        createMarkdownTagletStub(tagName, tagName, KEEP_ALL, useArgumentValidator, alwaysValid())
    }

    private MarkdownTaglet createMarkdownTagletStub(String tagName, String renderName) {
        createMarkdownTagletStub(tagName, renderName, KEEP_ALL, true, alwaysValid())
    }

    private MarkdownTaglet createMarkdownTagletStub(String tagName, WhiteSpacePreserver whiteSpacePreserver) {
        createMarkdownTagletStub(tagName, tagName, whiteSpacePreserver, true, alwaysValid())
    }

    private MarkdownTaglet createMarkdownTagletStub(String tagName, ArgumentValidator argumentValidator) {
        createMarkdownTagletStub(tagName, tagName, KEEP_ALL, true, argumentValidator)
    }

    private MarkdownTaglet createMarkdownTagletStub(String tagName, Exception ex) {
        def exceptionRender = { String name, List<String> argList -> throw ex } as Render
        createMarkdownTagletStub(tagName, tagName, KEEP_ALL, true, alwaysValid(), exceptionRender)
    }


    private MarkdownTaglet createMarkdownTagletStub(
            String tagName,
            String renderName,
            WhiteSpacePreserver whiteSpacePreserver,
            boolean useArgumentValidator,
            ArgumentValidator argumentValidator
    ) {
        def defaultRender = { String name, List<String> argList -> doRender(name, argList) } as Render
        return createMarkdownTagletStub(tagName, renderName, whiteSpacePreserver, useArgumentValidator, argumentValidator, defaultRender);
    }

    private MarkdownTaglet createMarkdownTagletStub(
            String tagName, String renderName, WhiteSpacePreserver whiteSpacePreserver, boolean useArgumentValidator, ArgumentValidator argumentValidator, Render render
    ) {
        def markdownTaglet = Stub(MarkdownTaglet) { MarkdownTaglet markdownTaglet ->
            markdownTaglet.createNewInstance() >> markdownTaglet
            markdownTaglet.getName() >> tagName
            markdownTaglet.getWhiteSpacePreserver() >> whiteSpacePreserver
            markdownTaglet.getArgumentValidator() >> argumentValidator
            markdownTaglet.useArgumentValidator() >> useArgumentValidator
            markdownTaglet.render(_) >> { List<List<String>> listOfArguments -> render.markdown(renderName, listOfArguments[0]) }
            markdownTaglet.renderRaw(_) >> { List<String> arguments -> render.markdown(renderName, arguments) }
        }
        return markdownTaglet
    }

    private interface Render {
        String markdown(String renderName, List<String> argumentList) throws Exception;
    }

    private static String rendered(String name, String... args) {
        doRender(name, args.toList())
    }

    private static String doRender(String name, List<String> args) {
        name + "(" + args.join(",") + ")"
    }

    private static ArgumentValidator alwaysValid() {
        return { List<String> ars -> VR_VALID } as ArgumentValidator
    }
}