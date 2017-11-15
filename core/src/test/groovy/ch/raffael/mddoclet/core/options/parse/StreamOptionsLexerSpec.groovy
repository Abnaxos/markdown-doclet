package ch.raffael.mddoclet.core.options.parse

import spock.lang.Specification
import spock.lang.Unroll


/**
 * @author Raffael Herzog
 */
class StreamOptionsLexerSpec extends Specification {

    def "Options String without any pitfalls gets parsed correctly"() {
      expect:
        toWordList(/foo    bar "quoted '" 'quoted "' qux/) == ['foo', 'bar', /quoted '/, /quoted "/, 'qux']
    }

    def "Line counting works, also with mixed newline types"() {
      expect:
        toTokenList('1\n2\r3\r\n4\r\r6').collect({t -> t.line}) == [1, 2, 3, 4, 6]
    }

    def "Quoted words accept Java escapes"() {
      expect:
        toWordList('foo "with\\nseveral\\\\escapes\\u006bbar\\"quote" bar') == ['foo', 'with\nseveral\\escapeskbar"quote', 'bar']
    }

    def "Non-quoted words are returned verbatim, no escapes"() {
      expect:
        toWordList(/foo with\nescape \u006b \x/) == ['foo', 'with\\nescape', 'k', '\\x']
    }

    def "Comments are ignored, except as part of a word (quoted or unquoted)"() {
      expect:
        toWordList('foo #comment\n  bar#foo foo#  "foo # bar"\n# comment at start of line') ==
                ['foo', 'bar#foo', 'foo#', 'foo # bar']
    }

    @Unroll
    def "Invalid escape throws ParseException (#text)"() {
      when:
        toWordList(text)

      then:
        def e = thrown StreamOptionsLexer.ParseException
        ['Illegal escape sequence', 'Illegal unicode escape sequence' ].contains(e.error)

      where:
        text << ['"\\x"', '"\\u23f"', '"\\u23xy']
    }

    @Unroll
    def "Unterminated quoted string throws ParseException (#text)"() {
      when:
        toWordList(text)

      then:
        def e = thrown StreamOptionsLexer.ParseException
        e.error == 'Unterminated quoted string'

      where:
        text << ["'foo ", '"foo ']
    }

    private List<StreamOptionsLexer.Token> toTokenList(String input) {
        new StreamOptionsLexer(new StringReader(input)).toTokenList()
    }

    private List<String> toWordList(String input) {
        new StreamOptionsLexer(new StringReader(input)).toWordList()
    }

}
