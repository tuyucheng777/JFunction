package io.github.jfunction.core;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/// A disjoint union of two types — conventionally `Left` for errors
/// and `Right` for success values.
public sealed interface Either<L, R> permits Either.Left, Either.Right {

    record Left<L, R>(L value) implements Either<L, R> {
        public Left {
            Objects.requireNonNull(value, "value");
        }
    }

    record Right<L, R>(R value) implements Either<L, R> {
        public Right {
            Objects.requireNonNull(value, "value");
        }
    }

    static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    default boolean isLeft() {
        return this instanceof Left<L, R>;
    }

    default boolean isRight() {
        return this instanceof Right<L, R>;
    }

    default L left() {
        return switch (this) {
            case Left(var v) -> v;
            case Right<L, R> _ -> throw new NoSuchElementException("Either is Right");
        };
    }

    default R right() {
        return switch (this) {
            case Right(var v) -> v;
            case Left<L, R> _ -> throw new NoSuchElementException("Either is Left");
        };
    }

    default <T> Either<L, T> map(Function<? super R, ? extends T> mapper) {
        return switch (this) {
            case Right(var v) -> Either.right(mapper.apply(v));
            case Left(var l) -> Either.left(l);
        };
    }

    default <T> Either<L, T> flatMap(Function<? super R, ? extends Either<L, T>> mapper) {
        return switch (this) {
            case Right(var v) -> mapper.apply(v);
            case Left(var l) -> Either.left(l);
        };
    }

    default <T> Either<T, R> mapLeft(Function<? super L, ? extends T> mapper) {
        return switch (this) {
            case Left(var v) -> Either.left(mapper.apply(v));
            case Right(var r) -> Either.right(r);
        };
    }

    default R getOrElse(R fallback) {
        return switch (this) {
            case Right(var v) -> v;
            case Left<L, R> _ -> fallback;
        };
    }

    default R getOrElse(Supplier<? extends R> supplier) {
        return switch (this) {
            case Right(var v) -> v;
            case Left<L, R> _ -> supplier.get();
        };
    }

    default void ifLeft(Consumer<? super L> action) {
        if (this instanceof Left(var v)) {
            action.accept(v);
        }
    }

    default void ifRight(Consumer<? super R> action) {
        if (this instanceof Right(var v)) {
            action.accept(v);
        }
    }

    default <T> T fold(Function<? super L, ? extends T> onLeft, Function<? super R, ? extends T> onRight) {
        return switch (this) {
            case Left(var l) -> onLeft.apply(l);
            case Right(var r) -> onRight.apply(r);
        };
    }
}
