package ch.raffael.mddoclet.core.ast

import static ch.raffael.mddoclet.core.ast.DocNode.Type.*
import static ch.raffael.mddoclet.core.ast.RootDocNode.scanFullDocComment
import static ch.raffael.mddoclet.core.ast.RootDocNode.scanStrippedDocComment


/**
 * @author Raffael Herzog
 */
class LexerSpec extends AbstractAstSpec {

    def "Full comment without leading stars"() {
      when:
        push scanFullDocComment(' /** \nMy Comment \n*/  ')

      then:
        matches(next(), COMMENT_START, ' /**')
        matches(next(), TEXT, ' ')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, 'My Comment ')
        matches(next(), NEWLINE, '\n')
        matches(next(), COMMENT_END, '*/  ')
        noMoreChildren()
    }

    def "Full comment without doc start"() {
      when:
        push scanFullDocComment(' \nMy Comment */  ')

      then:
        matches(next(), TEXT, ' ')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, 'My Comment ')
        matches(next(), COMMENT_END, '*/  ')
        noMoreChildren()
    }

    def "Full comment without doc end"() {
      when:
        push scanFullDocComment('/** \nMy Comment \n  ')

      then:
        matches(next(), COMMENT_START, '/**')
        matches(next(), TEXT, ' ')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, 'My Comment ')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, '  ')
        noMoreChildren()
    }

    def "Full comment without any doc delimiters"() {
      when:
        push scanFullDocComment(' \nMy Comment \n  ')

      then:
        matches(next(), TEXT, ' ')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, 'My Comment ')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, '  ')
        noMoreChildren()
    }

    def "Comment delimiters are ignored by scanStrippedDocComment"() {
      when:
        push scanStrippedDocComment('  /**\nMyComment*/')

      then:
        matches(next(), TEXT, '  /**')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, 'MyComment*/')
        noMoreChildren()
    }

    def "The leading asterisk, the whitespaces before and one whitespace after are stripped as LEAD (if present)"() {
      when:
        push scanFullDocComment('/** * not leading\n * standard lead\n   *   more whitespace after lead\n*no space after asterisk\nno lead\n  no lead with whitespace')

      then:
        matches(next(), COMMENT_START, '/**')
        matches(next(), TEXT, ' * not leading')
        matches(next(), NEWLINE, '\n')
        matches(next(), LEAD, ' * ')
        matches(next(), TEXT, 'standard lead')
        matches(next(), NEWLINE, '\n')
        matches(next(), LEAD, '   * ')
        matches(next(), TEXT, '  more whitespace after lead')
        matches(next(), NEWLINE, '\n')
        matches(next(), LEAD, '*')
        matches(next(), TEXT, 'no space after asterisk')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, 'no lead')
        matches(next(), NEWLINE, '\n')
        matches(next(), TEXT, '  no lead with whitespace')
        noMoreChildren()
    }

    def "Mixed newlines"() {
      when:
        push scanStrippedDocComment('\n\r\r\r\n\n')

      then:
        matches(next(), NEWLINE, '\n')
        matches(next(), NEWLINE, '\r')
        matches(next(), NEWLINE, '\r')
        matches(next(), NEWLINE, '\r\n')
        matches(next(), NEWLINE, '\n')
        noMoreChildren()
    }

}
