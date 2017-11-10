package ch.raffael.mddoclet.core.util;

import java.util.Map;

import ch.raffael.nullity.Nullable;


/**
 * TODO: 10.11.17 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class MutableContextData extends ContextData {

    MutableContextData() {
        super();
    }

    MutableContextData(Map<Key<?>, Object> data) {
        super(data);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public <T> T put(Key<T> key, T value) {
        return (T)data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T remove(Key<T> key, T value) {
        return (T)data.remove(key);
    }

    @Override
    public ContextData readOnlyView() {
        return new ContextData(data);
    }

}
