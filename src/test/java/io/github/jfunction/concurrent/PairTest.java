package io.github.jfunction.concurrent;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {

    @Test
    void allRunsInParallel() throws InterruptedException {
        var result = Pair.all(
                () -> 1,
                () -> 2,
                () -> 3
        );
        assertTrue(result.isSuccess());
        assertEquals(List.of(1, 2, 3), result.get());
    }

    @Test
    void raceReturnsFirstSuccess() throws InterruptedException {
        var result = Pair.race(
                () -> "slow",
                () -> "fast"
        );
        assertTrue(result.isSuccess());
        assertNotNull(result.get());
    }
}
