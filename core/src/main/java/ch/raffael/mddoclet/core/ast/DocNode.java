package ch.raffael.mddoclet.core.ast;

import java.util.Objects;

import ch.raffael.mddoclet.core.util.DataStore;
import ch.raffael.nullity.Nullable;


/**
 * Generic doc node.
 *
 * Most of the node types are represented by objects of this class,
 * specifically:
 *
 *  *  {@link DocNode.Type.TEXT TEXT}
 *  *  {@link DocNode.Type.NEWLINE NEWLINE}
 *  *  {@link DocNode.Type.LEAD LEAD}
 *  *  {@link DocNode.Type.COMMENT_START COMMENT_START}
 *  *  {@link DocNode.Type.COMMENT_END COMMENT_END}
 *
 * @author Raffael Herzog
 */
public class DocNode {

    private final Type type;
    private TextRange textRange;

    @Nullable
    private DocNode parent;

    private final DataStore userData =  DataStore.create();

    DocNode(Type type, TextRange textRange) {
        this.type = type;
        this.textRange = textRange;
    }

    public static DocNode createTextNode(TextRange textRange) {
        return new DocNode(Type.TEXT, textRange);
    }

    public static DocNode createWhitespaceNode(TextRange textRange) {
        return new DocNode(Type.TEXT, textRange);
    }

    public static DocNode createNewlineNode(TextRange textRange) {
        return new DocNode(Type.NEWLINE, textRange);
    }

    public static TagDocNode createInlineTagNode(TextRange textRange) {
        return new TagDocNode(Type.INLINE_TAG, textRange);
    }

    public static TagDocNode createBlockTagNode(TextRange textRange) {
        return new TagDocNode(Type.BLOCK_TAG, textRange);
    }

    public static DocNode createLeadNode(TextRange textRange) {
        return new DocNode(Type.LEAD, textRange);
    }

    public static DocNode createCommentStartNode(TextRange textRange) {
        return new DocNode(Type.COMMENT_START, textRange);
    }

    public static DocNode createCommentEndNode(TextRange textRange) {
        return new DocNode(Type.COMMENT_END, textRange);
    }

    public static RootDocNode createRootNode(TextRange textRange) {
        return new RootDocNode(textRange);
    }

    public Type getType() {
        return type;
    }

    public TextRange getTextRange() {
        return textRange;
    }

    public void setTextRange(TextRange textRange) {
        this.textRange = textRange;
    }

    public DocNode withTextRange(TextRange textRange) {
        setTextRange(textRange);
        return this;
    }

    /**
     * Get the parent node, throws an {@link UnsupportedOperationException}
     * if this is a root node.
     *
     * @throws UnsupportedOperationException If this node is a root node.
     */
    public DocNode getParent() {
        if (parent==null) throw new IllegalStateException("Root node");
        return parent;
    }

    @SuppressWarnings("ObjectEquality")
    DocNode reparent(@Nullable DocNode newParent) {
        if (parent != newParent) {
            if (newParent != null && parent != null) {
                throw new IllegalStateException("Illegal reparent: " + parent + " -> " + newParent);
            }
            parent = newParent;
        }
        return this;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public <T extends DocNodeVisitor> T accept(T visitor) {
        if (type.hidden()) {
            visitor.visitHiddenDocNode(this);
        } else {
            assert type == Type.TEXT || type == Type.NEWLINE;
            visitor.visitDocTextNode(this);
        }
        return visitor;
    }

    public DataStore getUserData() {
        return userData;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + type + ":" + textRange
                + ":'" + textRange.getText().replace("\n", "\\n").replace("\r", "\\r")
                + "']";
    }

    @Nullable
    protected <T extends DocNode> T reparent(@Nullable T currentNode, @Nullable T newNode) {
        if (!Objects.equals(currentNode, newNode)) {
            if (currentNode != null) currentNode.reparent(null);
            if (newNode != null) newNode.reparent(this);
        }
        return newNode;
    }

    public enum Type {
        TEXT, WHITESPACE, NEWLINE,
        INLINE_TAG_START_MARKER, INLINE_TAG_END_MARKER, BLOCK_TAG_START_MARKER,
        INLINE_TAG, BLOCK_TAG,
        ROOT,
        LEAD, COMMENT_START, COMMENT_END;

        private boolean hidden;
        private boolean whitespace;

        static {
            for (Type t : values()) {
                t.hidden = oneOf(t, LEAD,
                        INLINE_TAG_START_MARKER, INLINE_TAG_END_MARKER, BLOCK_TAG_START_MARKER,
                        COMMENT_START, COMMENT_END);
                t.whitespace = oneOf(t, WHITESPACE, NEWLINE);
            }
        }
        private static boolean oneOf(Type type, Type... oneOf) {
            for (Type t : oneOf) {
                if (type == t) {
                    return true;
                }
            }
            return false;
        }

        public boolean hidden() {
            return hidden;
        }
        public boolean whitespace() {
            return whitespace;
        }
    }

}
