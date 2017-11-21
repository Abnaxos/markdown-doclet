package ch.raffael.mddoclet.core.ast;

/**
 * The root doc node.
 *
 * @author Raffael Herzog
 */
public final class RootDocNode extends DocNode {

    private boolean tagsScanned = false;

    RootDocNode(TextRange textRange) {
        super(Type.ROOT, textRange);
    }

    /**
     * Scan the AST for a doc comment that includes the comment delimiters.
     *
     * Note that this AST has no tag nodes yet, these must be scanned by
     * {@link #scanTags()}.
     *
     * @see #scanStrippedDocComment(String)
     * @see #scanDocComment(String, boolean)
     * @see #scanTags()
     */
    public static RootDocNode scanFullDocComment(String docComment) {
        return scanDocComment(docComment, true);
    }

    /**
     * Scan the AST for a doc comment that ignoring any comment delimiters
     * (none are expected to be present).
     *
     * Note that this AST has no tag nodes yet, these must be scanned by
     * {@link #scanTags()}.
     *
     * @see #scanFullDocComment(String)
     * @see #scanDocComment(String, boolean)
     * @see #scanTags()
     */
    public static RootDocNode scanStrippedDocComment(String docComment) {
        return scanDocComment(docComment, false);
    }

    /**
     * Scan the AST for a doc comment.
     *
     * Note that this AST has no tag nodes yet, these must be scanned by
     * {@link #scanTags()}.
     *
     * @param hasDocCommentDelimiters `true`, if the doc comment includes
     *                                comment delimiters, `false` otherwise
     *
     * @see #scanFullDocComment(String)
     * @see #scanStrippedDocComment(String)
     * @see #scanTags()
     */
    public static RootDocNode scanDocComment(String docComment, boolean hasDocCommentDelimiters) {
        RootDocNode root = new RootDocNode(TextRange.ofAll(docComment));
        Lexer lexer = new Lexer(docComment, hasDocCommentDelimiters);
        root.appendChildren(lexer.toList());
        return root;
    }

    /**
     * Scans this AST for tags and inserts the corresponding {@link
     * TagDocNode}s. This modifies the AST.
     *
     * @return this RootDocNode
     */
    public RootDocNode scanTags() {
        if (tagsScanned) {
            return this;
        }
        // TODO: 20.11.17 scan tags
        return this;
    }

    @Override
    public DocNode getParent() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRoot() {
        return true;
    }

    @Override
    public <T extends DocNodeVisitor> T accept(T visitor) {
        visitor.visitRootDocNode(this);
        return visitor;
    }
}
