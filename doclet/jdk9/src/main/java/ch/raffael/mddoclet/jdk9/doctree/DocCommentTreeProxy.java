package ch.raffael.mddoclet.jdk9.doctree;

import java.util.List;

import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
final class DocCommentTreeProxy extends DocTreeProxy<DocCommentTree> implements DocCommentTree {

    DocCommentTreeProxy(DocCommentTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public List<? extends DocTree> getFirstSentence() {
        return proxyFor(delegate.getFirstSentence());
    }

    @Override
    public List<? extends DocTree> getFullBody() {
        return proxyFor(delegate.getFullBody());
    }

    @Override
    public List<? extends DocTree> getBody() {
        return proxyFor(delegate.getBody());
    }

    @Override
    public List<? extends DocTree> getBlockTags() {
        return proxyFor(delegate.getBlockTags());
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitDocComment(this, data);
    }
}
