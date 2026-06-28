package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OptionTest {

    @Test
    void someGet() {
        var opt = Option.of(42);
        assertTrue(opt.isDefined());
        assertFalse(opt.isEmpty());
        assertEquals(42, opt.get());
    }

    @Test
    void noneIsEmpty() {
        var opt = Option.<Integer>none();
        assertTrue(opt.isEmpty());
        assertFalse(opt.isDefined());
        assertThrows(Exception.class, opt::get);
    }

    @Test
    void ofNullableWithValue() {
        var opt = Option.ofNullable("hello");
        assertTrue(opt.isDefined());
        assertEquals("hello", opt.get());
    }

    @Test
    void ofNullableWithNull() {
        var opt = Option.ofNullable(null);
        assertTrue(opt.isEmpty());
    }

    @Test
    void ofRejectsNull() {
        assertThrows(NullPointerException.class, () -> Option.of(null));
    }

    @Test
    void fromOptionalPresent() {
        var opt = Option.from(Optional.of("x"));
        assertTrue(opt.isDefined());
        assertEquals("x", opt.get());
    }

    @Test
    void fromOptionalEmpty() {
        var opt = Option.from(Optional.empty());
        assertTrue(opt.isEmpty());
    }

    @Test
    void mapTransformsValue() {
        assertEquals("3", Option.of(3).map(String::valueOf).get());
    }

    @Test
    void mapOnNoneReturnsNone() {
        assertTrue(Option.<Integer>none().map(n -> n * 2).isEmpty());
    }

    @Test
    void mapReturningNullBecomesNone() {
        assertTrue(Option.of(1).map(n -> null).isEmpty());
    }

    @Test
    void flatMapSuccess() {
        var result = Option.of(3)
                .flatMap(n -> n > 2 ? Option.of(n * 2) : Option.none());
        assertEquals(6, result.get());
    }

    @Test
    void flatMapToNone() {
        var result = Option.of(1)
                .flatMap(n -> n > 2 ? Option.of(n * 2) : Option.none());
        assertTrue(result.isEmpty());
    }

    @Test
    void flatMapOnNone() {
        var result = Option.<Integer>none().flatMap(n -> Option.of(n * 2));
        assertTrue(result.isEmpty());
    }

    @Test
    void filterKeepsMatching() {
        assertTrue(Option.of(10).filter(n -> n > 5).isDefined());
    }

    @Test
    void filterRemovesNonMatching() {
        assertTrue(Option.of(3).filter(n -> n > 5).isEmpty());
    }

    @Test
    void filterOnNone() {
        assertTrue(Option.<Integer>none().filter(n -> n > 0).isEmpty());
    }

    @Test
    void getOrElseValue() {
        assertEquals(42, Option.of(42).getOrElse(0));
        assertEquals(0, Option.<Integer>none().getOrElse(0));
    }

    @Test
    void getOrElseSupplier() {
        assertEquals(42, Option.of(42).getOrElse(() -> 0));
        assertEquals(0, Option.<Integer>none().getOrElse(() -> 0));
    }

    @Test
    void orElseReturnsAlternative() {
        var alt = Option.of(99);
        assertEquals(99, Option.<Integer>none().orElse(alt).get());
        assertEquals(1, Option.of(1).orElse(alt).get());
    }

    @Test
    void ifPresentCalledOnSome() {
        var list = new ArrayList<Integer>();
        Option.of(5).ifPresent(list::add);
        assertEquals(1, list.size());
        assertEquals(5, list.getFirst());
    }

    @Test
    void ifPresentNotCalledOnNone() {
        var list = new ArrayList<Integer>();
        Option.<Integer>none().ifPresent(list::add);
        assertTrue(list.isEmpty());
    }

    @Test
    void streamFromSome() {
        var items = Option.of("a").stream().toList();
        assertEquals(1, items.size());
        assertEquals("a", items.getFirst());
    }

    @Test
    void streamFromNone() {
        var items = Option.none().stream().toList();
        assertTrue(items.isEmpty());
    }

    @Test
    void toOptionalSome() {
        assertEquals(Optional.of(7), Option.of(7).toOptional());
    }

    @Test
    void toOptionalNone() {
        assertEquals(Optional.empty(), Option.none().toOptional());
    }

    @Test
    void matchBranches() {
        assertEquals("val:7", Option.of(7).match(v -> "val:" + v, () -> "empty"));
        assertEquals("empty", Option.<Integer>none().match(v -> "val:" + v, () -> "empty"));
    }
}
