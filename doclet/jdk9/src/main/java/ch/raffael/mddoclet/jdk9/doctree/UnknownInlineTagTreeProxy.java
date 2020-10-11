package ch.raffael.mddoclet.jdk9.doctree;

import java.util.List;

import com.sun.source.doctree.DocTree;
import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.UnknownInlineTagTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 05.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class UnknownInlineTagTreeProxy extends InlineTagTreeProxy<UnknownInlineTagTree> implements UnknownInlineTagTree {

    UnknownInlineTagTreeProxy(UnknownInlineTagTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public List<? extends DocTree> getContent() {
        return proxyFor(delegate.getContent());
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitUnknownInlineTag(this, data);
    }
}
