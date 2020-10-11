package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.DocTreeVisitor;
import com.sun.source.doctree.LiteralTree;
import com.sun.source.doctree.TextTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;
import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class LiteralTreeProxy extends InlineTagTreeProxy<LiteralTree> implements LiteralTree {

    LiteralTreeProxy(LiteralTree delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public TextTree getBody() {
        return proxyFor(delegate.getBody());
    }

    @Override
    @Nullable
    public <R, D> R accept(@NotNull DocTreeVisitor<R, D> visitor, @Nullable D data) {
        return visitor.visitLiteral(this, data);
    }
}
