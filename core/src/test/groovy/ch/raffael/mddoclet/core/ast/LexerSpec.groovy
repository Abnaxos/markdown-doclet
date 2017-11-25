package ch.raffael.mddoclet.core.ast


/**
 * @author Raffael Herzog
 */
class LexerSpec extends AbstractAstSpec {

    def "Full comment without leading stars"() {
      when:
        pushFullDocComment ' /** \nMy Comment \n*/  '

      then:
        commentStart next(), ' /**'
        whitespace next(), ' '
        newline next(), '\n'
        text next(), 'My'
        whitespace next(), ' '
        text next(), 'Comment'
        whitespace next(), ' '
        newline next(), '\n'
        commentEnd next(), '*/  '
        end()
    }

    def "Full comment without doc start"() {
      when:
        pushFullDocComment ' \nMy Comment */  '

      then:
        whitespace next(), ' '
        newline next(), '\n'
        text next(), 'My'
        whitespace next(), ' '
        text next(), 'Comment'
        whitespace next(), ' '
        commentEnd next(), '*/  '
        end()
    }

    def "Full comment without doc end"() {
      when:
        pushFullDocComment '/** \nMy Comment \n  '

      then:
        commentStart next(), '/**'
        whitespace next(), ' '
        newline next(), '\n'
        text next(), 'My'
        whitespace next(), ' '
        text next(), 'Comment'
        whitespace next(), ' '
        newline next(), '\n'
        whitespace next(), '  '
        end()
    }

    def "Full comment without any doc delimiters"() {
      when:
        pushFullDocComment ' \nMy Comment \n  '

      then:
        whitespace next(), ' '
        newline next(), '\n'
        text next(), 'My'
        whitespace next(), ' '
        text next(), 'Comment'
        whitespace next(), ' '
        newline next(), '\n'
        whitespace next(), '  '
        end()
    }

    def "Comment delimiters are ignored by scanStrippedDocComment"() {
      when:
        pushStrippedDocComment '  /**\nMyComment*/'

      then:
        whitespace next(), '  '
        text next(), '/**'
        newline next(), '\n'
        text next(), 'MyComment*/'
        end()
    }

    def "The leading asterisk, the whitespaces before and one whitespace after are stripped as LEAD (if present)"() {
      when:
        pushFullDocComment '/** * not leading\n * standard lead\n   *   whitespace after lead\n*no whitespace\nno lead\n  no lead whitespace'

      then:
        commentStart next(), '/**'
        whitespace next(), ' '
        text next(), '*'
        whitespace next(), ' '
        text next(), 'not'
        whitespace next(), ' '
        text next(), 'leading'
        newline next(), '\n'
        lead next(), ' * '
        text next(), 'standard'
        whitespace next(), ' '
        text next(), 'lead'
        newline next(), '\n'
        lead next(), '   * '
        whitespace next(), '  '
        text next(), 'whitespace'
        whitespace next(), ' '
        text next(), 'after'
        whitespace next(), ' '
        text next(), 'lead'
        newline next(), '\n'
        lead next(), '*'
        text next(), 'no'
        whitespace next(), ' '
        text next(), 'whitespace'
        newline next(), '\n'
        text next(), 'no'
        whitespace next(), ' '
        text next(), 'lead'
        newline next(), '\n'
        whitespace next(), '  '
        text next(), 'no'
        whitespace next(), ' '
        text next(), 'lead'
        whitespace next(), ' '
        text next(), 'whitespace'
        end()
    }

    def "Mixed newlines"() {
      when:
        pushStrippedDocComment '\n\r\r\r\n\n'

      then:
        newline next(), '\n'
        newline next(), '\r'
        newline next(), '\r'
        newline next(), '\r\n'
        newline next(), '\n'
        end()
    }

    def "Inline Tags"() {
      when:
        pushStrippedDocComment '{@javadoc content}'

      then:
        inlineTagStartMarker next(), '{@'
        text next(), 'javadoc'
        whitespace next(), ' '
        text next(), 'content'
        inlineTagEndMarker next(), '}'
        end()

      when:
        pop()
        pushStrippedDocComment '{{wiki-style content}}'

      then:
        inlineTagStartMarker next(), '{{'
        text next(), 'wiki-style'
        whitespace next(), ' '
        text next(), 'content'
        inlineTagEndMarker next(), '}}'
        end()
    }

    def "Inline Tags without content"() {
      when:
        pushStrippedDocComment '{@javadoc}'

      then:
        inlineTagStartMarker next(), '{@'
        text next(), 'javadoc'
        inlineTagEndMarker next(), '}'
        end()

      when:
        pop()
        pushStrippedDocComment('{{wiki-style}}')

      then:
        inlineTagStartMarker next(), '{{'
        text next(), 'wiki-style'
        inlineTagEndMarker next(), '}}'
        end()
    }

    def "Inline Tags: Tag end marker matching depending on tag start"() {
      when:
        pushStrippedDocComment '}{@javadoc content}}'

      then:
        text next(), '}'
        inlineTagStartMarker next(), '{@'
        text next(), 'javadoc'
        whitespace next(), ' '
        text next(), 'content'
        inlineTagEndMarker next(), '}'
        text next(), '}'
        end()

      when:
        pop()
        pushStrippedDocComment '{{{wiki-style content}}}'

      then:
        text next(), '{'
        inlineTagStartMarker next(), '{{'
        text next(), 'wiki-style'
        whitespace next(), ' '
        text next(), 'content'
        inlineTagEndMarker next(), '}}'
        text next(), '}'
        end()
    }

    def "Nested inline tags"() {
      when:
        pushStrippedDocComment '{@javadoc foo{{nested-wiki wiki}}}'

      then:
        inlineTagStartMarker next(), '{@'
        text next(), 'javadoc'
        whitespace next(), ' '
        text next(), 'foo'
        inlineTagStartMarker next(), '{{'
        text next(), 'nested-wiki'
        whitespace next(), ' '
        text next(), 'wiki'
        inlineTagEndMarker next(), '}}'
        inlineTagEndMarker next(), '}'
        end()

      when:
        pop()
        pushStrippedDocComment '{{{wiki-style content}}}'

      then:
        text next(), '{'
        inlineTagStartMarker next(), '{{'
        text next(), 'wiki-style'
        whitespace next(), ' '
        text next(), 'content'
        inlineTagEndMarker next(), '}}'
        text next(), '}'
        end()
    }

    def "Unterminated wiki tags"() {
      when:
        pushStrippedDocComment '{@unterminated foo '

      then:
        inlineTagStartMarker next(), '{@'
        text next(), 'unterminated'
        whitespace next(), ' '
        text next(), 'foo'
        whitespace next(), ' '
        end()

      when:
        pop()
        pushStrippedDocComment '{{unterminated foo } '

      then:
        inlineTagStartMarker next(), '{{'
        text next(), 'unterminated'
        whitespace next(), ' '
        text next(), 'foo'
        whitespace next(), ' '
        text next(), '}'
        whitespace next(), ' '
        end()
    }

    def "Block tags"() {
      when:
        pushStrippedDocComment 'foo\n@block content\nmore-content\n * @block-with-lead content'

      then:
        text next(), 'foo'
        newline next()
        blockTagStartMarker next(), '@'
        text next(), 'block'
        whitespace next(), ' '
        text next(), 'content'
        newline next()
        text next(), 'more-content'
        newline next()
        lead next()
        blockTagStartMarker next()
        text next(), 'block-with-lead'
        whitespace next()
        text next(), 'content'
        end()
    }

    def "Inline tags as block tag content"() {
      when:
        pushStrippedDocComment 'foo\n@block {{inline}}\nmore-content\n * @block-with-lead {@inline}'

      then:
        text next(), 'foo'
        newline next()
        blockTagStartMarker next(), '@'
        text next(), 'block'
        whitespace next(), ' '
        inlineTagStartMarker next(), '{{'
        text next(), 'inline'
        inlineTagEndMarker next(), '}}'
        newline next()
        text next(), 'more-content'
        newline next()
        lead next()
        blockTagStartMarker next()
        text next(), 'block-with-lead'
        whitespace next()
        inlineTagStartMarker next(), '{@'
        text next(), 'inline'
        inlineTagEndMarker next(), '}'
        end()
    }

    def "Don't recognise block tags within the line"() {
      when:
        pushStrippedDocComment 'foo @no-block bar\n* foo @no-block bar'

      then:
        text next(), 'foo'
        whitespace next(), ' '
        text next(), '@no-block'
        whitespace next(), ' '
        text next(), 'bar'
        newline next()
        lead next(), '* '
        text next(), 'foo'
        whitespace next(), ' '
        text next(), '@no-block'
        whitespace next(), ' '
        text next(), 'bar'
        end()
    }

    def "Block tags cancel unterminated inline tags"() {
      when:
        pushStrippedDocComment '{{foo \n@block }}'

      then:
        inlineTagStartMarker next(), '{{'
        text next(), 'foo'
        whitespace next(), ' '
        newline next()
        blockTagStartMarker next()
        text next(), 'block'
        whitespace next(), ' '
        text next(), '}}'
        end()
    }

    private pushStrippedDocComment(String docComment) {
        push new Lexer(docComment, false).toList()
    }

    private pushFullDocComment(String docComment) {
        push new Lexer(docComment, true).toList()
    }

}
