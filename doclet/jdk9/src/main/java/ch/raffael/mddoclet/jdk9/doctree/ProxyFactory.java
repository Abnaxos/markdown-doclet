package ch.raffael.mddoclet.jdk9.doctree;

import com.sun.source.doctree.DocTree;


/**
 * TODO: 05.04.18 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class ProxyFactory {

    private final static ProxyFactory INSTANCE = new ProxyFactory();

    private ProxyFactory() {
    }

    public static ProxyFactory getInstance() {
        return INSTANCE;
    }

    public <T extends DocTree> T proxyFor(T delegate) {
        // FIXME: implement this
        return null;
    }

}
