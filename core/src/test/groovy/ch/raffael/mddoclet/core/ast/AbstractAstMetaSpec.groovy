package ch.raffael.mddoclet.core.ast

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
            appendChildren([
                    createTextNode(TextRange.ofStartAndLength(source, 0, 1)),
                    createTextNode(TextRange.ofStartAndLength(source, 1, 1)).with {
                        def subSource = 'ab'
                        appendChild(createTextNode(TextRange.ofStartAndLength(subSource, 0, 1)))
                        appendChild(createTextNode(TextRange.ofStartAndLength(subSource, 1, 1)))
                        return it
                    },
                    createTextNode(TextRange.ofStartAndLength(source, 2, 1)),
                    createTextNode(TextRange.ofStartAndLength(source, 3, 1)),
            ])
            return it
        }

      when:
        next()
      then:
        matches(current(), TEXT, '0')
        matches(next(), TEXT, '1')

      when:
        pushCurrent()
      then:
        matches(next(), TEXT, 'a')
        matches(next(), TEXT, 'b')
        noMoreChildren()

      when:
        pop()
      then:
        matches(current(), TEXT, '1')
        matches(next(), TEXT, '2')

      when:
        push(current())
      then:
        noMoreChildren()
      when:
        next()
      then:
        thrown NoSuchElementException

      when:
        pop()
      then:
        matches(next(), TEXT, '3')
        noMoreChildren()
    }

}
