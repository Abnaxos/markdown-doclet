package ch.raffael.mddoclet.core.ast;

/**
 * Specialised `DocNode` for Javadoc tags (both inline and block). Used for
 * {@link DocNode.Type Type}s {@link DocNode.Type#INLINE_TAG INLINE_TAG} and
 * {@link DocNode.Type#BLOCK_TAG BLOCK_TAG}.
 *
 * @author Raffael Herzog
 */
public final class TagDocNode extends DocNode {

    TagDocNode(Type type, TextRange textRange) {
        super(type, textRange);
    }

    @Override
    public <T extends DocNodeVisitor> T accept(T visitor) {
        visitor.visitDocTagNode(this);
        return visitor;
    }
}
