package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void someGet() {
        var opt = Option.of(42);
        assertTrue(opt.isDefined());
        assertEquals(42, opt.get());
    }

    @Test
    void noneIsEmpty() {
        var opt = Option.<Integer>none();
        assertTrue(opt.isEmpty());
        assertThrows(Exception.class, opt::get);
    }

    @Test
    void mapAndFlatMap() {
        var result = Option.of(3)
                .map(n -> n * 2)
                .flatMap(n -> n > 5 ? Option.of(n) : Option.none());

        assertTrue(result.isDefined());
        assertEquals(6, result.get());
    }

    @Test
    void filterRemovesNonMatching() {
        assertTrue(Option.of(10).filter(n -> n > 5).isDefined());
        assertTrue(Option.of(3).filter(n -> n > 5).isEmpty());
    }

    @Test
    void matchBranches() {
        assertEquals("val:7", Option.of(7).match(v -> "val:" + v, () -> "empty"));
        assertEquals("empty", Option.<Integer>none().match(v -> "val:" + v, () -> "empty"));
    }
}
