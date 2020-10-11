package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.InlineTagTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public abstract class InlineTagTreeProxy<D extends InlineTagTree> extends DocTreeProxy<D> implements InlineTagTree {

    InlineTagTreeProxy(D delegate, MdDocletContext context) {
        super(delegate, context);
    }

    @Override
    public String getTagName() {
        return delegate.getTagName();
    }
}
