package ch.raffael.mddoclet.core.util;

import java.util.Map;

import ch.raffael.nullity.Nullable;


/**
 * TODO: 10.11.17 Javadoc?
 *
 * @author Raffael Herzog
 */
public final class DataStore extends ImmutableDataStore {

    DataStore() {
        super();
    }

    DataStore(Map<Key<?>, Object> data) {
        super(data);
    }

    public static DataStore create() {
        return new DataStore();
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
    public ImmutableDataStore immutableView() {
        return new ImmutableDataStore(data);
    }

}
