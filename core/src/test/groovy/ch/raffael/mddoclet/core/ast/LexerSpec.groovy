package ch.raffael.mddoclet.core.ast

import spock.lang.Specification

import static ch.raffael.mddoclet.core.ast.DocNode.Type.*
import static ch.raffael.mddoclet.core.ast.RootDocNode.scanFullDocComment
import static ch.raffael.mddoclet.core.ast.RootDocNode.scanStrippedDocComment


/**
 * @author Raffael Herzog
 */
class LexerSpec extends Specification {

    private RootDocNode ast
    private Iterator<DocNode> childIterator

    def "Full comment without leading stars"() {
      when:
        ast = scanFullDocComment(' /** \nMy Comment \n*/  ')

      then:
        matchChild(nextChild(), COMMENT_START, ' /**')
        matchChild(nextChild(), TEXT, ' ')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, 'My Comment ')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), COMMENT_END, '*/  ')
        noMoreChildren()
    }

    def "Full comment without doc start"() {
      when:
        ast = scanFullDocComment(' \nMy Comment */  ')

      then:
        matchChild(nextChild(), TEXT, ' ')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, 'My Comment ')
        matchChild(nextChild(), COMMENT_END, '*/  ')
        noMoreChildren()
    }

    def "Full comment without doc end"() {
      when:
        ast = scanFullDocComment('/** \nMy Comment \n  ')

      then:
        matchChild(nextChild(), COMMENT_START, '/**')
        matchChild(nextChild(), TEXT, ' ')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, 'My Comment ')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, '  ')
        noMoreChildren()
    }

    def "Full comment without any doc delimiters"() {
      when:
        ast = scanFullDocComment(' \nMy Comment \n  ')

      then:
        matchChild(nextChild(), TEXT, ' ')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, 'My Comment ')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, '  ')
        noMoreChildren()
    }

    def "Comment delimiters are ignored by scanStrippedDocComment"() {
      when:
        ast = scanStrippedDocComment('  /**\nMyComment*/')

      then:
        matchChild(nextChild(), TEXT, '  /**')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT , 'MyComment*/')
        noMoreChildren()
    }

    def "Leading whitespaces + * + whitespace? are stripped"() {
      when:
        ast = scanFullDocComment('/** * not leading\n * standard lead\n   *   more whitespace after lead\n*no space after asterisk\nno lead\n  no lead with whitespace')

      then:
        matchChild(nextChild(), COMMENT_START, '/**')
        matchChild(nextChild(), TEXT, ' * not leading')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), LEAD, ' * ')
        matchChild(nextChild(), TEXT, 'standard lead')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), LEAD, '   * ')
        matchChild(nextChild(), TEXT, '  more whitespace after lead')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), LEAD, '*')
        matchChild(nextChild(), TEXT, 'no space after asterisk')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, 'no lead')
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), TEXT, '  no lead with whitespace')
        noMoreChildren()
    }

    def "Mixed newlines"() {
      when:
        ast = scanStrippedDocComment('\n\r\r\r\n\n')

      then:
        matchChild(nextChild(), NEWLINE, '\n')
        matchChild(nextChild(), NEWLINE, '\r')
        matchChild(nextChild(), NEWLINE, '\r')
        matchChild(nextChild(), NEWLINE, '\r\n')
        matchChild(nextChild(), NEWLINE, '\n')
        noMoreChildren()
    }

    private static boolean matchChild(DocNode child, DocNode.Type type, String text) {
        child.type == type && child.textRange.text == text
    }

    private DocNode nextChild() {
        childIterator().next()
    }

    private boolean noMoreChildren() {
        !childIterator().hasNext()
    }

    private Iterator<DocNode> childIterator() {
        if (childIterator == null) {
            childIterator = ast.children.iterator()
        }
        return childIterator
    }

}
