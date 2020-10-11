package ch.raffael.mddoclet.jdk9.doctree;

import java.util.List;

import com.sun.source.doctree.DocTree;

import ch.raffael.mddoclet.jdk9.MdDocletContext;

import static com.google.common.collect.ImmutableList.toImmutableList;


/**
 * TODO: 04.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public abstract class DocTreeProxy<D extends DocTree> extends Proxy<D> implements DocTree {

    final MdDocletContext context;

    DocTreeProxy(D delegate, MdDocletContext context) {
        super(delegate);
        this.context = context;
    }

    @Override
    public Kind getKind() {
        return delegate.getKind();
    }

    protected static <T extends DocTree> T proxyFor(T delegate) {
        // FIXME: not implemented
        return null;
    }

    protected static <T extends DocTree> List<T> proxyFor(List<T> delegates) {
        return delegates.stream().map(DocTreeProxy::proxyFor).collect(toImmutableList());
    }

    protected static String toHtml(String markdown) {
        throw new AssertionError("not implemented");
    }

}
