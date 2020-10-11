package ch.raffael.mddoclet.jdk9.doctree;

import java.util.List;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.IndexTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class IndexTreeProxy extends InlineTagTreeProxy<IndexTree> implements IndexTree {

    IndexTreeProxy(IndexTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public DocTree getSearchTerm() {
        return proxyFor(delegate.getSearchTerm());
    }

    @Override
    public List<? extends DocTree> getDescription() {
        return proxyFor(delegate.getDescription());
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitIndex(this, data);
    }
}
