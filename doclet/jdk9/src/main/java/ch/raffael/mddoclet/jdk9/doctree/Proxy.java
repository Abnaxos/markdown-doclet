package ch.raffael.mddoclet.jdk9.doctree;

/**
 * A base class for proxies implementing `equals()`, `hashCode()` and
 * `toString()`.
 *
 * @author Raffael Herzog
 */
class Proxy<D> {

    protected final D delegate;

    Proxy(D delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        //noinspection ObjectEquality,SimplifiableIfStatement
        if (o == null || getClass() != o.getClass()) return false;
        return delegate.equals(((Proxy)o).delegate);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return "<Proxy for " + delegate.toString() + ">";
    }
}
