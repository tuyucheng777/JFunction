package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

    @Test
    void rightCreation() {
        var either = Either.<String, Integer>right(5);
        assertTrue(either.isRight());
        assertFalse(either.isLeft());
        assertEquals(5, either.right());
    }

    @Test
    void leftCreation() {
        var either = Either.<String, Integer>left("err");
        assertTrue(either.isLeft());
        assertFalse(either.isRight());
        assertEquals("err", either.left());
    }

    @Test
    void rightThrowsOnLeft() {
        var either = Either.<String, Integer>right(5);
        assertThrows(NoSuchElementException.class, either::left);
    }

    @Test
    void leftThrowsOnRight() {
        var either = Either.<String, Integer>left("err");
        assertThrows(NoSuchElementException.class, either::right);
    }

    @Test
    void rightMap() {
        var either = Either.<String, Integer>right(5).map(n -> n + 1);
        assertTrue(either.isRight());
        assertEquals(6, either.right());
    }

    @Test
    void leftSkipsMap() {
        var either = Either.<String, Integer>left("err").map(n -> n + 1);
        assertTrue(either.isLeft());
        assertEquals("err", either.left());
    }

    @Test
    void flatMapRight() {
        var result = Either.<String, Integer>right(5)
                .flatMap(n -> Either.right(n * 2));
        assertEquals(10, result.right());
    }

    @Test
    void flatMapRightToLeft() {
        var result = Either.<String, Integer>right(5)
                .flatMap(n -> Either.left("failed"));
        assertTrue(result.isLeft());
        assertEquals("failed", result.left());
    }

    @Test
    void flatMapOnLeft() {
        var result = Either.<String, Integer>left("err")
                .flatMap(n -> Either.right(n * 2));
        assertTrue(result.isLeft());
        assertEquals("err", result.left());
    }

    @Test
    void mapLeft() {
        var result = Either.<String, Integer>left("err").mapLeft(String::toUpperCase);
        assertEquals("ERR", result.left());
    }

    @Test
    void mapLeftOnRight() {
        var result = Either.<String, Integer>right(10).mapLeft(String::toUpperCase);
        assertTrue(result.isRight());
        assertEquals(10, result.right());
    }

    @Test
    void getOrElseValue() {
        assertEquals(5, Either.<String, Integer>right(5).getOrElse(0));
        assertEquals(0, Either.<String, Integer>left("err").getOrElse(0));
    }

    @Test
    void getOrElseSupplier() {
        assertEquals(5, Either.<String, Integer>right(5).getOrElse(() -> 0));
        assertEquals(0, Either.<String, Integer>left("err").getOrElse(() -> 0));
    }

    @Test
    void ifLeftCalled() {
        var list = new ArrayList<String>();
        Either.<String, Integer>left("x").ifLeft(list::add);
        assertEquals(1, list.size());
        assertEquals("x", list.getFirst());
    }

    @Test
    void ifLeftNotCalledOnRight() {
        var list = new ArrayList<String>();
        Either.<String, Integer>right(1).ifLeft(list::add);
        assertTrue(list.isEmpty());
    }

    @Test
    void ifRightCalled() {
        var list = new ArrayList<Integer>();
        Either.<String, Integer>right(7).ifRight(list::add);
        assertEquals(1, list.size());
        assertEquals(7, list.getFirst());
    }

    @Test
    void ifRightNotCalledOnLeft() {
        var list = new ArrayList<Integer>();
        Either.<String, Integer>left("err").ifRight(list::add);
        assertTrue(list.isEmpty());
    }

    @Test
    void fold() {
        assertEquals("ok:3", Either.<String, Integer>right(3).fold(l -> "fail:" + l, r -> "ok:" + r));
        assertEquals("fail:x", Either.<String, Integer>left("x").fold(l -> "fail:" + l, r -> "ok:" + r));
    }

    @Test
    void nullRejectionsLeft() {
        assertThrows(NullPointerException.class, () -> Either.left(null));
    }

    @Test
    void nullRejectionsRight() {
        assertThrows(NullPointerException.class, () -> Either.right(null));
    }
}
