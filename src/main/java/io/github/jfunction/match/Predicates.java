package io.github.jfunction.match;

import java.util.Objects;
import java.util.function.Predicate;

/// Reusable predicates, including guards for primitive pattern matching.
public final class Predicates {

    private Predicates() {}

    public static Predicate<Integer> isPositive() {
        return n -> n > 0;
    }

    public static Predicate<Integer> isNegative() {
        return n -> n < 0;
    }

    public static Predicate<Integer> inRange(int min, int maxInclusive) {
        return n -> n >= min && n <= maxInclusive;
    }

    public static <T> Predicate<T> notNull() {
        return Objects::nonNull;
    }

    public static <T> Predicate<T> isEqual(T expected) {
        return value -> Objects.equals(value, expected);
    }

    public static <T> Predicate<T> and(Predicate<T> first, Predicate<T> second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        return first.and(second);
    }

    public static <T> Predicate<T> or(Predicate<T> first, Predicate<T> second) {
        Objects.requireNonNull(first, "first");
        Objects.requireNonNull(second, "second");
        return first.or(second);
    }
}
