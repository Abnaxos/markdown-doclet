package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.DocRootTree;
import com.sun.source.doctree.DocTreeVisitor;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 05.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class DocRootTreeProxy extends InlineTagTreeProxy<DocRootTree> implements DocRootTree {

    DocRootTreeProxy(DocRootTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitDocRoot(this, data);
    }
}
