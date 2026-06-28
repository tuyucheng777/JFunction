package io.github.jfunction.stream;

import io.github.jfunction.core.Option;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/// Stream utilities bridging [Stream] with JFunction types.
public final class JStreams {

    private JStreams() {}

    public static <T> Stream<T> ofNullable(T value) {
        return value == null ? Stream.empty() : Stream.of(value);
    }

    public static <T> Stream<T> fromOption(Option<T> option) {
        return option.stream();
    }

    public static <T, R> Stream<R> mapOption(Stream<T> stream, Function<T, Option<R>> mapper) {
        return stream.flatMap(t -> mapper.apply(t).stream());
    }

    public static <T> Stream<T> filterPresent(Stream<Option<T>> stream) {
        return stream.flatMap(Option::stream);
    }

    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<T> predicate) {
        return stream.gather(JGatherers.takeWhile(predicate));
    }

    public static <T> Stream<T> dropWhile(Stream<T> stream, Predicate<T> predicate) {
        return stream.gather(JGatherers.dropWhile(predicate));
    }

    public static <T, R> Stream<R> scan(Stream<T> stream, R initial, java.util.function.BiFunction<R, T, R> scanner) {
        return stream.gather(JGatherers.scan(() -> initial, scanner));
    }
}
