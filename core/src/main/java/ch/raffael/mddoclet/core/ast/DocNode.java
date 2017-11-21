package ch.raffael.mddoclet.core.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.raffael.mddoclet.core.util.DataStore;


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
    private final TextRange textRange;

    private DocNode parent;
    private final ArrayList<DocNode> children = new ArrayList<>();

    private final DataStore userData =  DataStore.create();

    DocNode(Type type, TextRange textRange) {
        this.type = type;
        this.textRange = textRange;
    }

    public static DocNode createTextNode(TextRange textRange) {
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

    public static DocNode createHiddenTextNode(TextRange textRange) {
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

    /**
     * Get the parent node, throws an {@link UnsupportedOperationException}
     * if this is a root node.
     *
     * @throws UnsupportedOperationException If this node is a root node.
     */
    public DocNode getParent() {
        return parent;
    }

    public boolean isRoot() {
        return false;
    }

    public List<DocNode> getChildren() {
        return children;
    }

    public void appendChild(DocNode child) {
        children.add(child);
    }

    public void appendChildren(Collection<? extends DocNode> children) {
        this.children.addAll(children);
    }

    public void insertChildBefore(DocNode after, DocNode newChild) {
        children.add(requireIndexOfChild(after), newChild);
    }

    public void insertChildAfter(DocNode after, DocNode newChild) {
        children.add(requireIndexOfChild(after) + 1, newChild);
    }

    public void replaceChild(DocNode after, DocNode... newChildren) {
        int index = requireIndexOfChild(after);
        if (newChildren.length == 0) {
            children.remove(index);
        } else {
            children.set(index, newChildren[0]);
            children.ensureCapacity(children.size() + newChildren.length - 1);
            for (int i = 1; i < newChildren.length; i++) {
                children.add(index + i, newChildren[i]);
            }
        }
    }

    public void replaceChild(DocNode after, Collection<? extends DocNode> newChildren) {
        int index = requireIndexOfChild(after);
        switch (newChildren.size()) {
        case 0:
            children.remove(index);
            break;
        case 1:
            if (newChildren instanceof List) {
                children.set(index, (DocNode)((List)newChildren).get(index));
            } else {
                children.set(index, newChildren.iterator().next());
            }
            break;
        default:
            children.remove(index);
            children.addAll(index, newChildren);
        }
    }

    public void removeChild(DocNode child) {
        children.remove(child);
    }

    private int requireIndexOfChild(DocNode after) {
        int index = children.indexOf(after);
        if (index < 0) {
            throw new IllegalArgumentException("Node " + this + " has no child " + after);
        }
        return index;
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

    enum Type {
        TEXT, NEWLINE,
        INLINE_TAG, BLOCK_TAG,
        ROOT,
        LEAD, COMMENT_START, COMMENT_END;

        public boolean hidden() {
            return ordinal() >= LEAD.ordinal();
        }
    }

}
