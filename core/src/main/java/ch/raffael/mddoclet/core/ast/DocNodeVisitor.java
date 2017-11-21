package ch.raffael.mddoclet.core.ast;

/**
 * Visitor for {@link DocNode}s.
 *
 * @author Raffael Herzog
 */
public interface DocNodeVisitor {

    /**
     * Visit nodes of types {@link DocNode.Type#TEXT TEXT} and {@link
     * DocNode.Type#NEWLINE NEWLINE}.
     */
    void visitDocTextNode(DocNode node);

    /**
     * Visit doc tag nodes.
     */
    void visitDocTagNode(TagDocNode node);

    /**
     * Visit hidden nodes.
     */
    default void visitHiddenDocNode(DocNode node) {
    }

    /**
     * Visit the root node.
     */
    default void visitRootDocNode(RootDocNode node) {

    }

}
