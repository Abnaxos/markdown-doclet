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

    private final DocNodeList content;

    RootDocNode(TextRange textRange) {
        this(textRange, null);
    }

    RootDocNode(TextRange textRange, @Nullable List<? extends DocNode> content) {
        super(Type.ROOT, textRange);
        this.content = DocNodeList.ofNullableList(this, content);
    }

    public static RootDocNode create(TextRange textRange) {
        return new RootDocNode(textRange);
    }

    public static RootDocNode parseFullDocComment(String docComment) {
        return parseDocComment(docComment, true);
    }

    public static RootDocNode parseStrippedDocComment(String docComment) {
        return parseDocComment(docComment, false);
    }

    public static RootDocNode parseDocComment(String docComment, boolean includesCommentDelimiters) {
        return new Parser(docComment, includesCommentDelimiters).parse();
    }

    public DocNodeList getContent() {
        return content;
    }

}
