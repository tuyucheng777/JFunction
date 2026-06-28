package io.github.jfunction.concurrent;

import io.github.jfunction.core.Either;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;

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
    void allEmptyTasks() throws InterruptedException {
        @SuppressWarnings("unchecked")
        var result = Pair.all(new Callable[0]);
        assertTrue(result.isSuccess());
        assertEquals(List.of(), result.get());
    }

    @Test
    void allWithFailure() throws InterruptedException {
        var result = Pair.all(
                () -> 1,
                () -> { throw new RuntimeException("boom"); },
                () -> 3
        );
        assertTrue(result.isFailure());
        assertEquals("boom", result.cause().getMessage());
    }

    @Test
    void raceReturnsFirstSuccess() throws InterruptedException {
        var result = Pair.race(
                () -> { Thread.sleep(500); return "slow"; },
                () -> "fast"
        );
        assertTrue(result.isSuccess());
        assertNotNull(result.get());
    }

    @Test
    void raceAllFail() throws InterruptedException {
        var result = Pair.race(
                () -> { throw new RuntimeException("fail1"); },
                () -> { throw new RuntimeException("fail2"); }
        );
        assertTrue(result.isFailure());
    }

    @Test
    void raceEmptyTasks() throws InterruptedException {
        @SuppressWarnings("unchecked")
        var result = Pair.race(new Callable[0]);
        assertTrue(result.isFailure());
        assertInstanceOf(IllegalArgumentException.class, result.cause());
    }

    @Test
    void mapAllSuccess() throws InterruptedException {
        var result = Pair.mapAll(List.of(1, 2, 3), n -> n * 10);
        assertTrue(result.isSuccess());
        assertEquals(List.of(10, 20, 30), result.get());
    }

    @Test
    void mapAllEmptyInput() throws InterruptedException {
        var result = Pair.mapAll(List.<Integer>of(), n -> n * 10);
        assertTrue(result.isSuccess());
        assertEquals(List.of(), result.get());
    }

    @Test
    void mapAllWithFailure() throws InterruptedException {
        var result = Pair.mapAll(List.of(1, 2, 0), n -> {
            if (n == 0) throw new ArithmeticException("div by zero");
            return 10 / n;
        });
        assertTrue(result.isFailure());
    }

    @Test
    void gatherCollectsSuccessAndFailure() throws InterruptedException {
        var results = Pair.gather(
                () -> "ok",
                () -> { throw new RuntimeException("err"); }
        );
        assertEquals(2, results.size());

        boolean hasRight = results.stream().anyMatch(Either::isRight);
        boolean hasLeft = results.stream().anyMatch(Either::isLeft);
        assertTrue(hasRight);
        assertTrue(hasLeft);
    }

    @Test
    void gatherAllSuccess() throws InterruptedException {
        var results = Pair.gather(
                () -> "a",
                () -> "b",
                () -> "c"
        );
        assertEquals(3, results.size());
        assertTrue(results.stream().allMatch(Either::isRight));
    }

    @Test
    void gatherEmpty() throws InterruptedException {
        @SuppressWarnings("unchecked")
        var results = Pair.gather(new Callable[0]);
        assertTrue(results.isEmpty());
    }
}
