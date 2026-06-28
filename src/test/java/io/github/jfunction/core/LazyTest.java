package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LazyTest {

    @Test
    void computesOnce() {
        var counter = new int[]{0};
        var lazy = Lazy.of(() -> {
            counter[0]++;
            return "value";
        });

        assertFalse(lazy.isInitialized());
        assertEquals("value", lazy.get());
        assertEquals("value", lazy.get());
        assertTrue(lazy.isInitialized());
        assertEquals(1, counter[0]);
    }

    @Test
    void ofValueIsImmediatelyAvailable() {
        var lazy = Lazy.of("direct");
        assertEquals("direct", lazy.get());
        assertTrue(lazy.isInitialized());
    }

    @Test
    void mapDefersEvaluation() {
        var counter = new int[]{0};
        var lazy = Lazy.of(() -> {
            counter[0]++;
            return 10;
        }).map(n -> n * 2);

        assertEquals(0, counter[0]);
        assertEquals(20, lazy.get());
        assertEquals(1, counter[0]);
    }

    @Test
    void mapCachesResult() {
        var counter = new int[]{0};
        var lazy = Lazy.of(() -> {
            counter[0]++;
            return 5;
        }).map(n -> n + 1);

        assertEquals(6, lazy.get());
        assertEquals(6, lazy.get());
        assertEquals(1, counter[0]);
    }

    @Test
    void flatMapDefersEvaluation() {
        var counter = new int[]{0};
        var lazy = Lazy.of(() -> {
            counter[0]++;
            return 3;
        }).flatMap(n -> Lazy.of(() -> n * 10));

        assertEquals(0, counter[0]);
        assertEquals(30, lazy.get());
        assertEquals(1, counter[0]);
    }

    @Test
    void flatMapCachesResult() {
        var counter = new int[]{0};
        var lazy = Lazy.of(() -> {
            counter[0]++;
            return 7;
        }).flatMap(n -> Lazy.of(n + 1));

        assertEquals(8, lazy.get());
        assertEquals(8, lazy.get());
        assertEquals(1, counter[0]);
    }

    @Test
    void isInitializedBeforeAndAfter() {
        var lazy = Lazy.of(() -> "hello");
        assertFalse(lazy.isInitialized());
        lazy.get();
        assertTrue(lazy.isInitialized());
    }

    @Test
    void nullSupplierThrows() {
        assertThrows(NullPointerException.class, () -> Lazy.of((java.util.function.Supplier<?>) null));
    }
}
