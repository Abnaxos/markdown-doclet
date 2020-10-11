package ch.raffael.mddoclet.jdk9.doctree;

import java.util.List;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.LinkTree;
import com.sun.source.doctree.ReferenceTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class LinkTreeProxy extends InlineTagTreeProxy<LinkTree> implements LinkTree {

    LinkTreeProxy(LinkTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public ReferenceTree getReference() {
        return proxyFor(delegate.getReference());
    }

    @Override
    public List<? extends DocTree> getLabel() {
        return proxyFor(delegate.getLabel());
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitLink(this, data);
    }
}
