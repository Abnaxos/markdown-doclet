package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.ReferenceTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class ReferenceTreeProxy extends DocTreeProxy<ReferenceTree> implements ReferenceTree {

    ReferenceTreeProxy(ReferenceTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public String getSignature() {
        return delegate.getSignature();
    }

    @Override
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitReference(this, data);
    }
}
