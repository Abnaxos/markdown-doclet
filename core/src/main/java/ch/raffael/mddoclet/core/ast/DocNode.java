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

    public static DocNode createText(TextRange textRange) {
        return new DocNode(Type.TEXT, textRange);
    }

    public static DocNode createWhitespace(TextRange textRange) {
        return new DocNode(Type.TEXT, textRange);
    }

    public static DocNode createNewline(TextRange textRange) {
        return new DocNode(Type.NEWLINE, textRange);
    }

    public static DocNode createLead(TextRange textRange) {
        return new DocNode(Type.LEAD, textRange);
    }

    public static DocNode createInlineTagStartMarker(TextRange textRange) {
        return new DocNode(Type.INLINE_TAG_START_MARKER, textRange);
    }

    public static DocNode createInlineTagEndMarker(TextRange textRange) {
        return new DocNode(Type.INLINE_TAG_END_MARKER, textRange);
    }

    public static DocNode createBlockTagStartMarker(TextRange textRange) {
        return new DocNode(Type.BLOCK_TAG_START_MARKER, textRange);
    }

    public static DocNode createCommentStart(TextRange textRange) {
        return new DocNode(Type.COMMENT_START, textRange);
    }

    public static DocNode createCommentEnd(TextRange textRange) {
        return new DocNode(Type.COMMENT_END, textRange);
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
        if (newParent == this) {
            throw new IllegalStateException("Illegal reparent: Cannot be parent of self");
        }
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
        switch (type) {
        case TEXT:
        case WHITESPACE:
        case NEWLINE:
            visitor.visitDocTextNode(this);
            break;
        case INLINE_TAG_START_MARKER:
        case INLINE_TAG_END_MARKER:
        case BLOCK_TAG_START_MARKER:
        case LEAD:
        case COMMENT_START:
        case COMMENT_END:
            visitor.visitHiddenDocNode(this);
            break;
        case INLINE_TAG:
        case BLOCK_TAG:
            visitor.visitDocTagNode((TagDocNode)this);
            break;
        case ROOT:
            visitor.visitDocTagNode((TagDocNode)this);
            break;
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
        LEAD, COMMENT_START, COMMENT_END,
        ROOT;

        private boolean text;
        private boolean hidden;
        private boolean whitespace;
        private boolean tag;

        static {
            for (Type t : values()) {
                t.text = oneOf(t, TEXT, WHITESPACE, NEWLINE);
                t.hidden = oneOf(t, LEAD,
                        INLINE_TAG_START_MARKER, INLINE_TAG_END_MARKER, BLOCK_TAG_START_MARKER,
                        COMMENT_START, COMMENT_END);
                t.whitespace = oneOf(t, WHITESPACE, NEWLINE);
                t.tag = oneOf(t, INLINE_TAG, BLOCK_TAG);
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

        public boolean isText() {
            return text;
        }

        public boolean isHidden() {
            return hidden;
        }

        public boolean isWhitespace() {
            return whitespace;
        }
        public boolean isTag() {
            return tag;
        }
    }

}
