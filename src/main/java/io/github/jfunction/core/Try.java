package io.github.jfunction.core;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/// Represents a computation that may succeed with a value or fail with an exception.
public sealed interface Try<T> permits Try.Success, Try.Failure {

    record Success<T>(T value) implements Try<T> {
        public Success {
            Objects.requireNonNull(value, "value");
        }
    }

    record Failure<T>(Throwable cause) implements Try<T> {
        public Failure {
            Objects.requireNonNull(cause, "cause");
        }
    }

    static <T> Try<T> of(Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        try {
            return success(supplier.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }

    static <T> Try<T> ofChecked(CheckedSupplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        try {
            return success(supplier.get());
        } catch (Throwable t) {
            return failure(t);
        }
    }

    static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Try<T> failure(Throwable cause) {
        return new Failure<>(cause);
    }

    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return this instanceof Failure<T>;
    }

    default T get() {
        return switch (this) {
            case Success(var v) -> v;
            case Failure(var e) -> throw new NoSuchElementException("Try failed", e);
        };
    }

    default Throwable cause() {
        return switch (this) {
            case Failure(var e) -> e;
            case Success<T> _ -> throw new NoSuchElementException("Try succeeded");
        };
    }

    default T getOrElse(T fallback) {
        return switch (this) {
            case Success(var v) -> v;
            case Failure<T> _ -> fallback;
        };
    }

    default T getOrElse(Supplier<? extends T> supplier) {
        return switch (this) {
            case Success(var v) -> v;
            case Failure<T> _ -> supplier.get();
        };
    }

    default <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        return switch (this) {
            case Success(var v) -> Try.of(() -> mapper.apply(v));
            case Failure(var e) -> new Failure<>(e);
        };
    }

    default <U> Try<U> flatMap(Function<? super T, ? extends Try<? extends U>> mapper) {
        return switch (this) {
            case Success(var v) -> flatten(mapper.apply(v));
            case Failure(var e) -> new Failure<>(e);
        };
    }

    private static <U> Try<U> flatten(Try<? extends U> result) {
        if (result == null) {
            return failure(new NullPointerException("flatMap returned null"));
        }
        return switch (result) {
            case Success(var u) -> success(u);
            case Failure(var e) -> failure(e);
        };
    }

    default Try<T> recover(Function<? super Throwable, ? extends T> handler) {
        return switch (this) {
            case Success<T> s -> s;
            case Failure(var e) -> Try.of(() -> handler.apply(e));
        };
    }

    default void ifSuccess(Consumer<? super T> action) {
        if (this instanceof Success(var v)) {
            action.accept(v);
        }
    }

    default void ifFailure(Consumer<? super Throwable> action) {
        if (this instanceof Failure(var e)) {
            action.accept(e);
        }
    }

    default <R> R fold(Function<? super Throwable, ? extends R> onFailure,
                       Function<? super T, ? extends R> onSuccess) {
        return switch (this) {
            case Failure(var e) -> onFailure.apply(e);
            case Success(var v) -> onSuccess.apply(v);
        };
    }

    default Either<Throwable, T> toEither() {
        return switch (this) {
            case Success(var v) -> Either.right(v);
            case Failure(var e) -> Either.left(e);
        };
    }

    @FunctionalInterface
    interface CheckedSupplier<T> {
        T get() throws Exception;
    }
}
