package ch.raffael.mddoclet.core.ast
/**
 * @author Raffael Herzog
 */
class ParserSpec extends AbstractAstSpec {

    def "A correct doc comment with nested tags"() {
      when:
        push RootDocNode.parseFullDocComment("""/**
                * This method returns {{code null}}.
                *
                * @return Always {{code null}}.
                * @throws nothing
                */""")

      then:
        commentStart next()
        newline next()
        lead next()
        text next()
        whitespace next()
        text next()
        whitespace next()
        text next()
        whitespace next()

        inlineTag next()
        inlineTagStartMarker current().startMarker, '{{'
        inlineTagEndMarker current().endMarker, '}}'
        text current().name, 'code'
        push()
        whitespace next()
        text next()
        end()

        pop()
        text next()
        newline next()
        lead next()
        newline next()
        lead next()

        blockTag next()
        blockTagStartMarker current().startMarker
        current().endMarker == null
        text current().name, 'return'
        push()
        whitespace next()
        text next()
        whitespace next()

        inlineTag next()
        inlineTagStartMarker current().startMarker, '{{'
        inlineTagEndMarker current().endMarker, '}}'
        text current().name, 'code'
        push()
        whitespace next()
        text next()
        end()

        pop()
        text next()
        newline next()
        lead next()
        end()

        pop()
        blockTag next()
        blockTagStartMarker current().startMarker
        current().endMarker == null
        text current().name, 'throws'
        push()
        whitespace next()
        text next()
        newline next()
        whitespace next()
        end()

        pop()
        commentEnd next()
        end()
    }

}
