package io.github.jfunction.stream;

import io.github.jfunction.core.Option;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JStreamsTest {

    @Test
    void ofNullableWithValue() {
        var result = JStreams.ofNullable("hello").toList();
        assertEquals(List.of("hello"), result);
    }

    @Test
    void ofNullableWithNull() {
        var result = JStreams.ofNullable(null).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void fromOptionSome() {
        var result = JStreams.fromOption(Option.of(42)).toList();
        assertEquals(List.of(42), result);
    }

    @Test
    void fromOptionNone() {
        var result = JStreams.fromOption(Option.none()).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void mapOptionFiltersNone() {
        var result = JStreams.mapOption(
                Stream.of(1, 2, 3, 4, 5),
                n -> n % 2 == 0 ? Option.of(n * 10) : Option.none()
        ).toList();
        assertEquals(List.of(20, 40), result);
    }

    @Test
    void mapOptionAllSome() {
        var result = JStreams.mapOption(
                Stream.of("a", "b"),
                s -> Option.of(s.toUpperCase())
        ).toList();
        assertEquals(List.of("A", "B"), result);
    }

    @Test
    void filterPresent() {
        var stream = Stream.<Option<Integer>>of(Option.of(1), Option.none(), Option.of(3), Option.none());
        var result = JStreams.filterPresent(stream).toList();
        assertEquals(List.of(1, 3), result);
    }

    @Test
    void filterPresentAllNone() {
        var stream = Stream.<Option<Integer>>of(Option.none(), Option.none());
        var result = JStreams.filterPresent(stream).toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void takeWhile() {
        var result = JStreams.takeWhile(Stream.of(1, 2, 3, 4, 5), n -> n < 4).toList();
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void dropWhile() {
        var result = JStreams.dropWhile(Stream.of(1, 2, 3, 4, 5), n -> n < 3).toList();
        assertEquals(List.of(3, 4, 5), result);
    }

    @Test
    void scan() {
        var result = JStreams.scan(Stream.of(1, 2, 3), 0, Integer::sum).toList();
        assertEquals(List.of(1, 3, 6), result);
    }

    @Test
    void scanWithInitialValue() {
        var result = JStreams.scan(Stream.of(1, 2, 3), 10, Integer::sum).toList();
        assertEquals(List.of(11, 13, 16), result);
    }
}
