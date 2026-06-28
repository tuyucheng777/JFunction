package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    @Test
    void successPath() {
        var tr = Try.of(() -> "hello");
        assertTrue(tr.isSuccess());
        assertEquals("hello", tr.get());
    }

    @Test
    void failurePath() {
        var tr = Try.of(() -> { throw new RuntimeException("boom"); });
        assertTrue(tr.isFailure());
        assertEquals("boom", tr.cause().getMessage());
    }

    @Test
    void recover() {
        var tr = Try.<Integer>of(() -> { throw new RuntimeException("x"); })
                .recover(e -> 0);
        assertEquals(0, tr.get());
    }
}
