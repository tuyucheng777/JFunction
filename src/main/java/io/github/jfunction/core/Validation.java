package io.github.jfunction.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/// Accumulates validation errors (applicative style) instead of short-circuiting
/// on the first failure like [Either].
public sealed interface Validation<E, T> permits Validation.Valid, Validation.Invalid {

    record Valid<E, T>(T value) implements Validation<E, T> {
        public Valid {
            Objects.requireNonNull(value, "value");
        }
    }

    record Invalid<E, T>(List<E> errors) implements Validation<E, T> {
        public Invalid {
            errors = List.copyOf(Objects.requireNonNull(errors, "errors"));
            if (errors.isEmpty()) {
                throw new IllegalArgumentException("errors must not be empty");
            }
        }

        public Invalid(E error) {
            this(List.of(Objects.requireNonNull(error, "error")));
        }
    }

    static <E, T> Validation<E, T> valid(T value) {
        return new Valid<>(value);
    }

    static <E, T> Validation<E, T> invalid(E error) {
        return new Invalid<>(error);
    }

    static <E, T> Validation<E, T> invalid(List<E> errors) {
        return new Invalid<>(errors);
    }

    @SafeVarargs
    static <E, T> Validation<E, T> invalid(E first, E... rest) {
        var list = new ArrayList<E>();
        list.add(first);
        list.addAll(List.of(rest));
        return new Invalid<>(list);
    }

    default boolean isValid() {
        return this instanceof Valid<E, T>;
    }

    default boolean isInvalid() {
        return this instanceof Invalid<E, T>;
    }

    default T get() {
        return switch (this) {
            case Valid(var v) -> v;
            case Invalid(var errors) -> throw new IllegalStateException("Validation failed: " + errors);
        };
    }

    default List<E> errors() {
        return switch (this) {
            case Invalid(var errors) -> errors;
            case Valid<E, T> _ -> List.of();
        };
    }

    default <U> Validation<E, U> map(Function<? super T, ? extends U> mapper) {
        return switch (this) {
            case Valid(var v) -> valid(mapper.apply(v));
            case Invalid(var errors) -> new Invalid<>(errors);
        };
    }

    default <U> Validation<E, U> flatMap(Function<? super T, ? extends Validation<E, U>> mapper) {
        return switch (this) {
            case Valid(var v) -> mapper.apply(v);
            case Invalid(var errors) -> new Invalid<>(errors);
        };
    }

    default Validation<E, T> orElse(Validation<E, T> alternative) {
        return switch (this) {
            case Valid<E, T> v -> v;
            case Invalid<E, T> _ -> alternative;
        };
    }

    default T getOrElse(T fallback) {
        return switch (this) {
            case Valid(var v) -> v;
            case Invalid<E, T> _ -> fallback;
        };
    }

    default T getOrElse(Supplier<? extends T> supplier) {
        return switch (this) {
            case Valid(var v) -> v;
            case Invalid<E, T> _ -> supplier.get();
        };
    }

    /// Combine two validations, accumulating all errors.
    static <E, A, B, C> Validation<E, C> combine(
            Validation<E, A> va,
            Validation<E, B> vb,
            java.util.function.BiFunction<A, B, C> combiner) {

        return switch (va) {
            case Valid(var a) -> switch (vb) {
                case Valid(var b) -> valid(combiner.apply(a, b));
                case Invalid(var errors) -> new Invalid<>(errors);
            };
            case Invalid(var errorsA) -> switch (vb) {
                case Valid(var _) -> new Invalid<>(errorsA);
                case Invalid(var errorsB) -> {
                    var merged = Stream.concat(errorsA.stream(), errorsB.stream()).toList();
                    yield new Invalid<>(merged);
                }
            };
        };
    }

    default Either<List<E>, T> toEither() {
        return switch (this) {
            case Valid(var v) -> Either.right(v);
            case Invalid(var errors) -> Either.left(errors);
        };
    }
}
