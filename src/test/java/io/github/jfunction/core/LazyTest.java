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
    void mapDefersEvaluation() {
        var lazy = Lazy.of(() -> 10).map(n -> n * 2);
        assertEquals(20, lazy.get());
    }
}
