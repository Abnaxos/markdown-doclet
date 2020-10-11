package ch.raffael.mddoclet.core.util;

import java.util.Objects;
import java.util.function.Supplier;

import ch.raffael.nullity.Nullable;


/**
 * A lazy value applying double-checked locking patter using the given
 * supplier.
 *
 * @author Raffael Herzog
 */
@SuppressWarnings("unused")
public final class Cached<T> implements Supplier<T> {

    private final Supplier<? extends T> supplier;
    @Nullable
    private volatile T instance = null;

    private Cached(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public static <T> Cached<T> of(Supplier<? extends T> supplier) {
        return new Cached<T>(supplier);
    }

    @Override
    public T get() {
        T returning = instance;
        if (returning == null) {
            synchronized (this) {
                returning = instance;
                if (returning == null) {
                    returning = instance = Objects.requireNonNull(supplier.get());
                }
            }
        }
        assert returning != null;
        return returning;
    }

    public Cached<T> invalidate() {
        instance = null;
        return this;
    }

}
