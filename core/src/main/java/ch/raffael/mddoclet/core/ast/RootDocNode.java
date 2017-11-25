package ch.raffael.mddoclet.core.ast;

import java.util.List;

import ch.raffael.nullity.Nullable;


/**
 * The root doc node.
 *
 * @author Raffael Herzog
 */
public final class RootDocNode extends DocNode {

    private boolean tagsScanned = false;

    @Nullable
    private DocNode commentStart = null;
    @Nullable
    private DocNode commentEnd = null;
    private final DocNodeList content;

    RootDocNode(TextRange textRange) {
        this(textRange, null);
    }

    RootDocNode(TextRange textRange, @Nullable List<? extends DocNode> content) {
        super(Type.ROOT, textRange);
        this.content = DocNodeList.ofNullableList(this, content);
    }

    @Nullable
    public DocNode getCommentStart() {
        return commentStart;
    }

    public void setCommentStart(@Nullable DocNode commentStart) {
        this.commentStart = commentStart;
    }

    public RootDocNode withCommentStart(@Nullable DocNode commentStart) {
        this.commentStart = commentStart;
        return this;
    }

    @Nullable
    public DocNode getCommentEnd() {
        return commentEnd;
    }

    public void setCommentEnd(@Nullable DocNode commentEnd) {
        this.commentEnd = commentEnd;
    }

    public RootDocNode withCommentEnd(@Nullable DocNode commentEnd) {
        this.commentEnd = commentEnd;
        return this;
    }

    public DocNodeList getContent() {
        return content;
    }

    @Override
    public <T extends DocNodeVisitor> T accept(T visitor) {
        visitor.visitRootDocNode(this);
        return visitor;
    }
}
