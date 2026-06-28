package io.github.jfunction.stream;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Gatherer;
import java.util.stream.Stream;

/// Custom [Gatherer] implementations for functional stream processing.
/// Uses the finalized Stream Gatherers API (JEP 485, since JDK 24).
public final class JGatherers {

    private JGatherers() {}

    /// Prefix scan — emit the accumulated result after each element.
    public static <T, R> Gatherer<T, ?, R> scan(Supplier<R> initial, BiFunction<R, T, R> scanner) {
        Objects.requireNonNull(initial, "initial");
        Objects.requireNonNull(scanner, "scanner");

        class State {
            R current = initial.get();
        }

        return Gatherer.<T, State, R>ofSequential(
                State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    state.current = scanner.apply(state.current, element);
                    return downstream.push(state.current);
                })
        );
    }

    /// Take elements while the predicate holds, then stop (inclusive of first failure).
    public static <T> Gatherer<T, ?, T> takeWhile(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        class State {
            boolean done;
        }

        return Gatherer.<T, State, T>ofSequential(
                State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    if (state.done) {
                        return false;
                    }
                    if (predicate.test(element)) {
                        return downstream.push(element);
                    }
                    state.done = true;
                    return false;
                })
        );
    }

    /// Drop elements while the predicate holds, then emit the rest.
    public static <T> Gatherer<T, ?, T> dropWhile(Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");

        class State {
            boolean dropping = true;
        }

        return Gatherer.<T, State, T>ofSequential(
                State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    if (state.dropping) {
                        if (predicate.test(element)) {
                            return true;
                        }
                        state.dropping = false;
                    }
                    return downstream.push(element);
                })
        );
    }

    /// Map each element to zero or more elements (flatMap for streams).
    public static <T, R> Gatherer<T, ?, R> flatMap(Function<T, Stream<R>> mapper) {
        Objects.requireNonNull(mapper, "mapper");
        return Gatherer.<T, Void, R>ofSequential(
                () -> null,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    try (Stream<R> inner = mapper.apply(element)) {
                        inner.forEach(downstream::push);
                    }
                    return true;
                })
        );
    }

    /// Group consecutive equal elements (run-length encoding style).
    public static <T> Gatherer<T, ?, java.util.List<T>> groupConsecutive() {
        class State {
            T last;
            final ArrayList<T> group = new ArrayList<>();
        }

        return Gatherer.<T, State, java.util.List<T>>ofSequential(
                State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) -> {
                    if (state.last == null || Objects.equals(state.last, element)) {
                        state.group.add(element);
                        state.last = element;
                        return true;
                    }
                    var result = java.util.List.copyOf(state.group);
                    state.group.clear();
                    state.group.add(element);
                    state.last = element;
                    return downstream.push(result);
                }),
                (state, downstream) -> {
                    if (!state.group.isEmpty()) {
                        downstream.push(java.util.List.copyOf(state.group));
                    }
                }
        );
    }

    /// Emit elements indexed with their position.
    public static <T> Gatherer<T, ?, java.util.Map.Entry<Long, T>> indexed() {
        class State {
            long index;
        }

        return Gatherer.<T, State, java.util.Map.Entry<Long, T>>ofSequential(
                State::new,
                Gatherer.Integrator.ofGreedy((state, element, downstream) ->
                        downstream.push(java.util.Map.entry(state.index++, element))
                )
        );
    }
}
