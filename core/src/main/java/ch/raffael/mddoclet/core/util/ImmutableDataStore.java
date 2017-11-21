package ch.raffael.mddoclet.core.util;


import java.util.HashMap;
import java.util.Map;

import ch.raffael.nullity.Nullable;


/**
 * A type-safe data store for any data. Can be used to attach arbitrary data
 * to objects that provide a data store.
 *
 * The date store is designed such that the keys cannot be accessed by
 * iterating through all data. This way, the visibility of the key is kept,
 * i.e. code that has no access to the key used to store the data, has no
 * way to gain access to that data. If the key is private, so is the data
 * attached to it.
 *
 * In most cases, you'll probably want to use the {@link DataStore}, which
 * is mutable. This class is mainly used for defensive copies.
 *
 * @see DataStore
 *
 * @author Raffael Herzog
 */
public class ImmutableDataStore {

    final Map<Key<?>, Object> data;

    ImmutableDataStore() {
        this(new HashMap<>());
    }

    ImmutableDataStore(Map<Key<?>, Object> data) {
        this.data = data;
    }

    public static ImmutableDataStore create() {
        return new ImmutableDataStore();
    }

    public <T> T get(Key<T> key) {
        T value = internalGet(key);
        if ( value == null ) {
            throw new IllegalStateException("No object registered for key " + key);
        }
        return value;
    }

    @Nullable
    public <T> T get(Key<T> key, @Nullable T fallback) {
        T value = internalGet(key);
        return value == null ? fallback : value;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private <T> T internalGet(Key<T> key) {
        return (T)data.get(key);
    }

    public ImmutableDataStore immutableCopy() {
        return new ImmutableDataStore(new HashMap<>(data));
    }

    public DataStore mutableCopy() {
        return new DataStore(new HashMap<>(data));
    }

    /**
     * Get an immutable view on this data store. Changes to this store will
     * still be visible in the immutable view.
     *
     * @return An immutable view of this data store.
     */
    public ImmutableDataStore immutableView() {
        return this;
    }

    public static <T> Key<T> key() {
        return new DefaultKey<>();
    }

    public static <T> Key<T> key(Class<?> type) {
        return new DefaultKey<>(type);
    }

    public static <T> Key<T> key(String name) {
        return new DefaultKey<>(name);
    }

    @SuppressWarnings("unused")
    interface Key<T> {
    }

    public static class DefaultKey<T> implements Key<T> {
        private final String name;

        public DefaultKey() {
            this("ContextData.DefaultKey");
        }

        public DefaultKey(Class<?> type) {
            this(type.getName());
        }

        public DefaultKey(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
