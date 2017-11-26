package ch.raffael.mddoclet.core.ast;


import java.util.List;

import ch.raffael.nullity.Nullable;


/**
 * Specialised `DocNode` for Javadoc tags (both inline and block). Used for
 * {@link DocNode.Type Type}s {@link DocNode.Type#INLINE_TAG INLINE_TAG} and
 * {@link DocNode.Type#BLOCK_TAG BLOCK_TAG}.
 *
 * @author Raffael Herzog
 */
public final class TagDocNode extends DocNode {

    @Nullable
    private DocNode startMarker;
    @Nullable
    private DocNode endMarker;
    @Nullable
    private DocNode name;
    private final DocNodeList content;

    TagDocNode(Type type, TextRange textRange) {
        this(type, textRange, null);
    }

    TagDocNode(Type type, TextRange textRange, @Nullable List<? extends DocNode> content) {
        super(type, textRange);
        assert type == Type.BLOCK_TAG || type == Type.INLINE_TAG;
        this.content = DocNodeList.ofNullableList(this, content);
    }

    public static TagDocNode createInlineTag(TextRange textRange) {
        return new TagDocNode(Type.INLINE_TAG, textRange);
    }

    public static TagDocNode createBlockTag(TextRange textRange) {
        return new TagDocNode(Type.BLOCK_TAG, textRange);
    }

    @Nullable
    public DocNode getStartMarker() {
        return startMarker;
    }

    public void setStartMarker(DocNode startMarker) {
        this.startMarker = reparent(this.startMarker, startMarker);
    }

    public TagDocNode withStartMarker(DocNode startMarker) {
        setStartMarker(startMarker);
        return this;
    }

    @Nullable
    public DocNode getEndMarker() {
        return endMarker;
    }

    public void setEndMarker(@Nullable DocNode endMarker) {
        this.endMarker = reparent(this.endMarker, endMarker);
    }

    public TagDocNode withEndMarker(DocNode endMarker) {
        setEndMarker(endMarker);
        return this;
    }

    @Nullable
    public DocNode getName() {
        return name;
    }

    public void setName(DocNode name) {
        this.name = reparent(this.name, name);
    }

    public TagDocNode withName(DocNode name) {
        setName(name);
        return this;
    }

    public DocNodeList getContent() {
        return content;
    }
}
