package ch.raffael.mddoclet.core.ast

import spock.lang.Specification


/**
 * Abstract spec that provides some utilities for checking trees of {@link
 * DocNode}s.
 *
 * @author Raffael Herzog
 */
abstract class AbstractAstSpec extends Specification {

    private final List<NodeIterator> stack = new LinkedList<>()

    static boolean matches(DocNode child, DocNode.Type type, String text) {
        child.type == type && child.textRange.text == text
    }

    /**
     * Push a node on the stack. All calls to
     *
     *   - {@link #next()}
     *   - {@link #current()}
     *   - {@link #end()}
     *
     * will delegate to the child iterator of this node until {@link #pop()}
     * is called.
     *
     * @param node The node to push on the stack.
     */
    void push(DocNode node) {
        stack.add(new NodeIterator(node))
    }

    /**
     * Push a list of nodes on the stack. All calls to
     *
     *   - {@link #next()}
     *   - {@link #current()}
     *   - {@link #end()}
     *
     * will delegate to the child iterator of this node until {@link #pop()}
     * is called.
     *
     * @param node The node to push on the stack.
     */
    void push(List<? extends DocNode> nodes) {
        stack.add(new NodeIterator(nodes))
    }

    /**
     * Push the {@link #current()} child node on the stack.
     *
     * @see #push(DocNode)
     */
    void push() {
        push(current())
    }

    /**
     * Pop the top node from the stack.
     */
    void pop() {
        stack.remove(stack.size() - 1)
    }

    private NodeIterator peek() {
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
     * @see #next()
     */
    DocNode getNext() {
        next()
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
     * @see #current()
     */
    DocNode getCurrent() {
        current()
    }

    /**
     * Query whether the stack's top node has more children.
     *
     * @return `true` if the current top node has no more children.
     */
    boolean end() {
        peek().noMoreChildren()
    }

    static boolean text(DocNode node, String text = null) {
        node.type == DocNode.Type.TEXT && (text == null || text == node.textRange.text)
    }

    static boolean whitespace(DocNode node, String text = null) {
        node.type == DocNode.Type.WHITESPACE && (text == null || text == node.textRange.text)
    }

    static boolean newline(DocNode node, String text = null) {
        node.type == DocNode.Type.NEWLINE && (text == null || text == node.textRange.text)
    }

    static boolean inlineTagStartMarker(DocNode node, String text = null) {
        node.type == DocNode.Type.INLINE_TAG_START_MARKER && (text == null || text == node.textRange.text)
    }

    static boolean inlineTagEndMarker(DocNode node, String text = null) {
        node.type == DocNode.Type.INLINE_TAG_END_MARKER && (text == null || text == node.textRange.text)
    }

    static boolean blockTagStartMarker(DocNode node, String text = null) {
        node.type == DocNode.Type.BLOCK_TAG_START_MARKER && (text == null || text == node.textRange.text)
    }

    static boolean inlineTag(DocNode node, String text = null) {
        node.type == DocNode.Type.INLINE_TAG && (text == null || text == node.textRange.text)
    }

    static boolean blockTag(DocNode node, String text = null) {
        node.type == DocNode.Type.BLOCK_TAG && (text == null || text == node.textRange.text)
    }

    static boolean lead(DocNode node, String text = null) {
        node.type == DocNode.Type.LEAD && (text == null || text == node.textRange.text)
    }

    static boolean commentStart(DocNode node, String text = null) {
        node.type == DocNode.Type.COMMENT_START && (text == null || text == node.textRange.text)
    }

    static boolean commentEnd(DocNode node, String text = null) {
        node.type == DocNode.Type.COMMENT_END && (text == null || text == node.textRange.text)
    }

    private static final class NodeIterator {
        private final Iterator<? extends DocNode> iterator
        private DocNode current
        private NodeIterator(DocNode node) {
            //noinspection GrUnresolvedAccess
            this(node.content as List)
        }
        private NodeIterator(List<? extends DocNode> list) {
            this.iterator = list.iterator()
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
