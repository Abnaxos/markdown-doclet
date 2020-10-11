package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.ReferenceTree;
import com.sun.source.doctree.ValueTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class ValueTreeProxy extends InlineTagTreeProxy<ValueTree> implements ValueTree {

    ValueTreeProxy(ValueTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public ReferenceTree getReference() {
        return proxyFor(delegate.getReference());
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitValue(this, data);
    }
}
