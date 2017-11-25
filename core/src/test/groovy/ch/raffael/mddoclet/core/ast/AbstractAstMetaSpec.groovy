package ch.raffael.mddoclet.core.ast

import static ch.raffael.mddoclet.core.ast.DocNode.Type.INLINE_TAG
import static ch.raffael.mddoclet.core.ast.DocNode.Type.TEXT
import static ch.raffael.mddoclet.core.ast.DocNode.createRootNode


/**
 * @author Raffael Herzog
 */
class AbstractAstMetaSpec extends AbstractAstSpec {

    def "Check that the inherited infrastructure works"() {
      given:
        push createRootNode(TextRange.ofAll('0123')).with {
            def source = it.textRange.text
            content.addAll([
                    createTextNode(TextRange.ofStartAndLength(source, 0, 1)),
                    createInlineTagNode(TextRange.ofStartAndLength(source, 1, 1)).with {
                        def subSource = 'ab'
                        content.add createTextNode(TextRange.ofStartAndLength(subSource, 0, 1))
                        content.add createTextNode(TextRange.ofStartAndLength(subSource, 1, 1))
                        return it
                    },
                    createInlineTagNode(TextRange.ofStartAndLength(source, 2, 1)),
                    createTextNode(TextRange.ofStartAndLength(source, 3, 1)),
            ])
            return it
        }

      when:
        next()
      then:
        matches(current(), TEXT, '0')
        matches(next(), INLINE_TAG, '1')

      when:
        pushCurrent()
      then:
        matches(next(), TEXT, 'a')
        matches(next(), TEXT, 'b')
        end()

      when:
        pop()
      then:
        matches(current(), INLINE_TAG, '1')
        matches(next(), INLINE_TAG, '2')

      when:
        push(current())
      then:
        end()
      when:
        next()
      then:
        thrown NoSuchElementException

      when:
        pop()
      then:
        matches(next(), TEXT, '3')
        end()
    }

}
