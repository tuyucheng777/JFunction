package io.github.jfunction.stream;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class JGatherersTest {

    @Test
    void scanAccumulates() {
        var result = Stream.of(1, 2, 3, 4)
                .gather(JGatherers.scan(() -> 0, Integer::sum))
                .toList();
        assertEquals(List.of(1, 3, 6, 10), result);
    }

    @Test
    void scanWithStringConcat() {
        var result = Stream.of("a", "b", "c")
                .gather(JGatherers.scan(() -> "", (acc, s) -> acc + s))
                .toList();
        assertEquals(List.of("a", "ab", "abc"), result);
    }

    @Test
    void takeWhileStopsEarly() {
        var result = Stream.of(1, 2, 3, 4, 5)
                .gather(JGatherers.takeWhile(n -> n < 4))
                .toList();
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void takeWhileAllMatch() {
        var result = Stream.of(1, 2, 3)
                .gather(JGatherers.takeWhile(n -> n < 10))
                .toList();
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void takeWhileNoneMatch() {
        var result = Stream.of(5, 6, 7)
                .gather(JGatherers.takeWhile(n -> n < 5))
                .toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void dropWhileSkipsPrefix() {
        var result = Stream.of(1, 2, 3, 4, 5)
                .gather(JGatherers.dropWhile(n -> n < 3))
                .toList();
        assertEquals(List.of(3, 4, 5), result);
    }

    @Test
    void dropWhileNoneDropped() {
        var result = Stream.of(5, 6, 7)
                .gather(JGatherers.dropWhile(n -> n < 5))
                .toList();
        assertEquals(List.of(5, 6, 7), result);
    }

    @Test
    void dropWhileAllDropped() {
        var result = Stream.of(1, 2, 3)
                .gather(JGatherers.dropWhile(n -> n < 10))
                .toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void flatMapExpandsElements() {
        var result = Stream.of(1, 2, 3)
                .gather(JGatherers.flatMap(n -> Stream.of(n, n * 10)))
                .toList();
        assertEquals(List.of(1, 10, 2, 20, 3, 30), result);
    }

    @Test
    void flatMapEmpty() {
        var result = Stream.of(1, 2, 3)
                .gather(JGatherers.flatMap(n -> Stream.empty()))
                .toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void groupConsecutiveBasic() {
        var result = Stream.of(1, 1, 2, 2, 2, 3)
                .gather(JGatherers.groupConsecutive())
                .toList();
        assertEquals(List.of(List.of(1, 1), List.of(2, 2, 2), List.of(3)), result);
    }

    @Test
    void groupConsecutiveAllSame() {
        var result = Stream.of("a", "a", "a")
                .gather(JGatherers.groupConsecutive())
                .toList();
        assertEquals(List.of(List.of("a", "a", "a")), result);
    }

    @Test
    void groupConsecutiveAllDifferent() {
        var result = Stream.of(1, 2, 3)
                .gather(JGatherers.groupConsecutive())
                .toList();
        assertEquals(List.of(List.of(1), List.of(2), List.of(3)), result);
    }

    @Test
    void groupConsecutiveSingleElement() {
        var result = Stream.of(42)
                .gather(JGatherers.groupConsecutive())
                .toList();
        assertEquals(List.of(List.of(42)), result);
    }

    @Test
    void groupConsecutiveEmpty() {
        var result = Stream.<Integer>empty()
                .gather(JGatherers.groupConsecutive())
                .toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void indexed() {
        var result = Stream.of("a", "b", "c")
                .gather(JGatherers.indexed())
                .map(e -> e.getKey() + ":" + e.getValue())
                .toList();
        assertEquals(List.of("0:a", "1:b", "2:c"), result);
    }

    @Test
    void indexedEmpty() {
        var result = Stream.<String>empty()
                .gather(JGatherers.indexed())
                .toList();
        assertTrue(result.isEmpty());
    }
}
