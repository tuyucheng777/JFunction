package io.github.jfunction.core;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/// An optional value — a richer alternative to [Optional] with
public sealed interface Option<T> permits Option.Some, Option.None {

    record Some<T>(T value) implements Option<T> {
        public Some {
            Objects.requireNonNull(value, "value");
        }
    }

    record None<T>() implements Option<T> {
        private static final None<?> INSTANCE = new None<>();

        @SuppressWarnings("unchecked")
        static <T> None<T> instance() {
            return (None<T>) INSTANCE;
        }
    }

    static <T> Option<T> of(T value) {
        return new Some<>(value);
    }

    static <T> Option<T> ofNullable(T value) {
        return value == null ? none() : of(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Option<T> none() {
        return None.instance();
    }

    static <T> Option<T> from(Optional<T> optional) {
        return optional.map(Option::of).orElseGet(Option::none);
    }

    default boolean isDefined() {
        return this instanceof Some<T>;
    }

    default boolean isEmpty() {
        return this instanceof None<T>;
    }

    default T get() {
        return switch (this) {
            case Some(var v) -> v;
            case None<T> _ -> throw new NoSuchElementException("Option is empty");
        };
    }

    default T getOrElse(T fallback) {
        return switch (this) {
            case Some(var v) -> v;
            case None<T> _ -> fallback;
        };
    }

    default T getOrElse(Supplier<? extends T> supplier) {
        return switch (this) {
            case Some(var v) -> v;
            case None<T> _ -> supplier.get();
        };
    }

    default <U> Option<U> map(Function<? super T, ? extends U> mapper) {
        return switch (this) {
            case Some(var v) -> ofNullable(mapper.apply(v));
            case None<T> _ -> none();
        };
    }

    default <U> Option<U> flatMap(Function<? super T, ? extends Option<? extends U>> mapper) {
        return switch (this) {
            case Some(var v) -> flatten(mapper.apply(v));
            case None<T> _ -> none();
        };
    }

    private static <U> Option<U> flatten(Option<? extends U> option) {
        if (option == null) {
            return none();
        }
        return switch (option) {
            case Some(var u) -> of(u);
            case None() -> none();
        };
    }

    default Option<T> filter(Predicate<? super T> predicate) {
        return switch (this) {
            case Some(var v) when predicate.test(v) -> this;
            case Some(var _) -> none();
            case None<T> _ -> none();
        };
    }

    default Option<T> orElse(Option<T> alternative) {
        return switch (this) {
            case Some<T> _ -> this;
            case None<T> _ -> alternative;
        };
    }

    default void ifPresent(Consumer<? super T> action) {
        if (this instanceof Some(var v)) {
            action.accept(v);
        }
    }

    default Stream<T> stream() {
        return switch (this) {
            case Some(var v) -> Stream.of(v);
            case None<T> _ -> Stream.empty();
        };
    }

    default Optional<T> toOptional() {
        return switch (this) {
            case Some(var v) -> Optional.of(v);
            case None<T> _ -> Optional.empty();
        };
    }

    /// Pattern-match on this option using Java 26 primitive-aware switch.
    default <R> R match(Function<? super T, ? extends R> onSome, Supplier<? extends R> onNone) {
        return switch (this) {
            case Some(var v) -> onSome.apply(v);
            case None<T> _ -> onNone.get();
        };
    }
}
