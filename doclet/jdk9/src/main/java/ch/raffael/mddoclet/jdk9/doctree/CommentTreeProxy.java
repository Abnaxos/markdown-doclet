package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.CommentTree;
import com.sun.source.doctree.DocTreeVisitor;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 08.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class CommentTreeProxy extends DocTreeProxy<CommentTree> implements CommentTree {

    CommentTreeProxy(CommentTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public String getBody() {
        return delegate.getBody();
    }

    @Override
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitComment(this, data);
    }
}
