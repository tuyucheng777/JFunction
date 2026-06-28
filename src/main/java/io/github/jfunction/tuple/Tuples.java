package io.github.jfunction.tuple;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

/// Immutable product types for functional composition.
public final class Tuples {

    private Tuples() {}

    public static <A, B> Tuple2<A, B> of(A a, B b) {
        return new Tuple2<>(a, b);
    }

    public static <A, B, C> Tuple3<A, B, C> of(A a, B b, C c) {
        return new Tuple3<>(a, b, c);
    }

    public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, C c, D d) {
        return new Tuple4<>(a, b, c, d);
    }

    public record Tuple2<A, B>(A _1, B _2) {
        public Tuple2 {
            Objects.requireNonNull(_1, "_1");
            Objects.requireNonNull(_2, "_2");
        }

        public <R> R apply(java.util.function.BiFunction<A, B, R> fn) {
            return fn.apply(_1, _2);
        }

        public Tuple2<B, A> swap() {
            return new Tuple2<>(_2, _1);
        }

        public <C> Tuple3<A, B, C> append(C c) {
            return new Tuple3<>(_1, _2, c);
        }

        public Map.Entry<A, B> toEntry() {
            return Map.entry(_1, _2);
        }
    }

    @FunctionalInterface
    public interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    public record Tuple3<A, B, C>(A _1, B _2, C _3) {
        public Tuple3 {
            Objects.requireNonNull(_1, "_1");
            Objects.requireNonNull(_2, "_2");
            Objects.requireNonNull(_3, "_3");
        }

        public <R> R apply(TriFunction<A, B, C, R> fn) {
            return fn.apply(_1, _2, _3);
        }

        public <D> Tuple4<A, B, C, D> append(D d) {
            return new Tuple4<>(_1, _2, _3, d);
        }
    }

    public record Tuple4<A, B, C, D>(A _1, B _2, C _3, D _4) {
        public Tuple4 {
            Objects.requireNonNull(_1, "_1");
            Objects.requireNonNull(_2, "_2");
            Objects.requireNonNull(_3, "_3");
            Objects.requireNonNull(_4, "_4");
        }
    }
}
