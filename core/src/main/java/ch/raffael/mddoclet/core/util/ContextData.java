package ch.raffael.mddoclet.core.util;


import java.util.HashMap;
import java.util.Map;

import ch.raffael.nullity.NotNull;
import ch.raffael.nullity.Nullable;


/**
 * TODO: 10.11.17 Javadoc?
 *
 * @author Raffael Herzog
 */
public class ContextData {

    final Map<Key<?>, Object> data;

    ContextData() {
        this(new HashMap<Key<?>, Object>());
    }

    ContextData(Map<Key<?>, Object> data) {
        this.data = data;
    }

    public static ContextData create() {
        return new ContextData();
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

    public ContextData readOnlyCopy(@Nullable Predicate<Key<?>> filter) {
        return new ContextData(copyMap(filter));
    }

    public ContextData mutableCopy(@Nullable Predicate<Key<?>> filter) {
        return new ContextData(copyMap(filter));
    }

    public ContextData readOnlyView() {
        return this;
    }

    @NotNull
    protected Map<Key<?>, Object> copyMap(@Nullable Predicate<Key<?>> filter) {
        Map<Key<?>, Object> copy = new HashMap<>();
        for ( Map.Entry<Key<?>, Object> entry : data.entrySet() ) {
            if ( filter == null || filter.test(entry.getKey()) ) {
                copy.put(entry.getKey(), entry.getValue());
            }
        }
        return copy;
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
