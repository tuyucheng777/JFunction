package io.github.jfunction.core;

import java.lang.LazyConstant;
import java.util.Objects;
import java.util.function.Supplier;

/// Deferred, memoized computation backed by Java 26 [LazyConstant].
///
/// Unlike a plain `Supplier`, the value is computed at most once and
/// treated as an immutable constant by the JVM after initialization.
public final class Lazy<T> implements Supplier<T> {

    private final LazyConstant<T> constant;

    private Lazy(LazyConstant<T> constant) {
        this.constant = constant;
    }

    public static <T> Lazy<T> of(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return new Lazy<>(LazyConstant.of(supplier::get));
    }

    public static <T> Lazy<T> of(T value) {
        Objects.requireNonNull(value, "value");
        return new Lazy<>(LazyConstant.of(() -> value));
    }

    @Override
    public T get() {
        return constant.get();
    }

    public boolean isInitialized() {
        return constant.isInitialized();
    }

    /// Transform the lazy value without forcing evaluation of the source.
    public <U> Lazy<U> map(java.util.function.Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return Lazy.of(() -> mapper.apply(this.get()));
    }

    /// Chain lazy computations — the inner lazy is only created when needed.
    public <U> Lazy<U> flatMap(java.util.function.Function<? super T, Lazy<U>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return Lazy.of(() -> mapper.apply(this.get()).get());
    }
}
