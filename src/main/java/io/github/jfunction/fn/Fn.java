package io.github.jfunction.fn;

import io.github.jfunction.core.Either;
import io.github.jfunction.core.Option;
import io.github.jfunction.core.Try;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/// Function composition, piping, and combinator utilities.
public final class Fn {

    private Fn() {}

    /// Left-to-right function composition: `f.andThen(g) == compose(g, f)`.
    @SafeVarargs
    public static <T> Function<T, T> pipe(Function<T, T>... stages) {
        Objects.requireNonNull(stages, "stages");
        return value -> {
            T current = value;
            for (var stage : stages) {
                current = stage.apply(current);
            }
            return current;
        };
    }

    public static <A, B, C> Function<A, C> compose(Function<B, C> after, Function<A, B> before) {
        Objects.requireNonNull(after, "after");
        Objects.requireNonNull(before, "before");
        return before.andThen(after);
    }

    public static <A, B, C> Function<A, C> andThen(Function<A, B> first, Function<B, C> second) {
        return first.andThen(second);
    }

    /// Currying for binary functions.
    public static <A, B, C> Function<A, Function<B, C>> curry(BiFunction<A, B, C> fn) {
        Objects.requireNonNull(fn, "fn");
        return a -> b -> fn.apply(a, b);
    }

    /// Uncurrying for binary functions.
    public static <A, B, C> BiFunction<A, B, C> uncurry(Function<A, Function<B, C>> fn) {
        Objects.requireNonNull(fn, "fn");
        return (a, b) -> fn.apply(a).apply(b);
    }

    /// Constant function — always returns the same value.
    public static <T, R> Function<T, R> constant(R value) {
        return _ -> value;
    }

    /// Identity function.
    public static <T> Function<T, T> identity() {
        return Function.identity();
    }

    /// Predicate negation.
    public static <T> Predicate<T> not(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        return predicate.negate();
    }

    /// Lift a function into Option context.
    public static <A, B> Function<Option<A>, Option<B>> liftOption(Function<A, B> fn) {
        Objects.requireNonNull(fn, "fn");
        return opt -> opt.map(fn);
    }

    /// Lift a function into Either right context.
    public static <L, A, B> Function<Either<L, A>, Either<L, B>> liftEither(Function<A, B> fn) {
        Objects.requireNonNull(fn, "fn");
        return either -> either.map(fn);
    }

    /// Lift a function into Try context.
    public static <A, B> Function<Try<A>, Try<B>> liftTry(Function<A, B> fn) {
        Objects.requireNonNull(fn, "fn");
        return tr -> tr.map(fn);
    }

    /// Tap — perform a side effect without changing the value.
    public static <T> Function<T, T> tap(Consumer<T> action) {
        Objects.requireNonNull(action, "action");
        return value -> {
            action.accept(value);
            return value;
        };
    }

    /// Memoize a supplier (simple, non-thread-safe). For thread-safe memoization use [io.github.jfunction.core.Lazy].
    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return io.github.jfunction.core.Lazy.of(supplier);
    }

    /// Partial application — fix the first argument.
    public static <A, B, C> Function<B, C> partial(BiFunction<A, B, C> fn, A fixed) {
        Objects.requireNonNull(fn, "fn");
        return b -> fn.apply(fixed, b);
    }
}
