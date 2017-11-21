package ch.raffael.mddoclet.core.ast

import spock.lang.Specification


/**
 * Abstract spec that provides some utilities for checking trees of {@link
 * DocNode}s.
 *
 * @author Raffael Herzog
 */
abstract class AbstractAstSpec extends Specification {

    private final List<Ast> stack = new LinkedList<>()

    static boolean matches(DocNode child, DocNode.Type type, String text) {
        child.type == type && child.textRange.text == text
    }

    /**
     * Push a node on the stack. All calls to
     *
     *   - {@link #next()}
     *   - {@link #current()}
     *   - {@link #noMoreChildren()}
     *
     * will delegate to the child iterator of this node until {@link #pop()}
     * is called.
     *
     * @param node The node to push on the stack.
     */
    void push(DocNode node) {
        stack.add(new Ast(node))
    }

    /**
     * Push the {@link #current()} child node on the stack.
     *
     * @see #push(DocNode)
     */
    void pushCurrent() {
        push(current())
    }

    /**
     * Pop the top node from the stack.
     */
    void pop() {
        stack.remove(stack.size() - 1)
    }

    private Ast peek() {
        return stack.get(stack.size() - 1)
    }

    /**
     * Move to the next child of the stack's top node.
     *
     * @return The next child node.
     */
    DocNode next() {
        peek().nextChild()
    }

    /**
     * Return the current child of the stack's top node.
     *
     * @return The current child node.
     */
    DocNode current() {
        peek().currentChild()
    }

    /**
     * Query whether the stack's top node has more children.
     *
     * @return `true` if the current top node has no more children.
     */
    boolean noMoreChildren() {
        peek().noMoreChildren()
    }

    private static final class Ast {
        private final DocNode root
        private final Iterator<DocNode> iterator
        private DocNode current
        private Ast(DocNode root) {
            this.root = root
            this.iterator = root.children.iterator()
        }
        private DocNode nextChild() {
            current = null
            current = iterator.next()
            return current
        }

        private DocNode currentChild() {
            if (current == null) {
                throw new IllegalStateException("No current child")
            }
            return current
        }
        private boolean noMoreChildren() {
            return !iterator.hasNext()
        }
    }

}
