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
    void takeWhileStopsEarly() {
        var result = Stream.of(1, 2, 3, 4, 5)
                .gather(JGatherers.takeWhile(n -> n < 4))
                .toList();
        assertEquals(List.of(1, 2, 3), result);
    }

    @Test
    void dropWhileSkipsPrefix() {
        var result = Stream.of(1, 2, 3, 4, 5)
                .gather(JGatherers.dropWhile(n -> n < 3))
                .toList();
        assertEquals(List.of(3, 4, 5), result);
    }

    @Test
    void indexed() {
        var result = Stream.of("a", "b")
                .gather(JGatherers.indexed())
                .map(e -> e.getKey() + ":" + e.getValue())
                .toList();
        assertEquals(List.of("0:a", "1:b"), result);
    }
}
