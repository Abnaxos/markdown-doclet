package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.InheritDocTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class InheritDocTreeProxy extends InlineTagTreeProxy<InheritDocTree> implements InheritDocTree {

    InheritDocTreeProxy(InheritDocTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitInheritDoc(this, data);
    }
}
