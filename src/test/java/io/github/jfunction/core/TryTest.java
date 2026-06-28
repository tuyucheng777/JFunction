package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    @Test
    void successPath() {
        var tr = Try.of(() -> "hello");
        assertTrue(tr.isSuccess());
        assertFalse(tr.isFailure());
        assertEquals("hello", tr.get());
    }

    @Test
    void failurePath() {
        var tr = Try.of(() -> { throw new RuntimeException("boom"); });
        assertTrue(tr.isFailure());
        assertFalse(tr.isSuccess());
        assertEquals("boom", tr.cause().getMessage());
    }

    @Test
    void getOnFailureThrows() {
        var tr = Try.of(() -> { throw new RuntimeException("x"); });
        assertThrows(NoSuchElementException.class, tr::get);
    }

    @Test
    void causeOnSuccessThrows() {
        var tr = Try.of(() -> "ok");
        assertThrows(NoSuchElementException.class, tr::cause);
    }

    @Test
    void ofCheckedSuccess() {
        var tr = Try.ofChecked(() -> Integer.parseInt("42"));
        assertTrue(tr.isSuccess());
        assertEquals(42, tr.get());
    }

    @Test
    void ofCheckedFailure() {
        var tr = Try.ofChecked(() -> Integer.parseInt("abc"));
        assertTrue(tr.isFailure());
        assertInstanceOf(NumberFormatException.class, tr.cause());
    }

    @Test
    void successFactory() {
        var tr = Try.success(10);
        assertTrue(tr.isSuccess());
        assertEquals(10, tr.get());
    }

    @Test
    void failureFactory() {
        var ex = new IllegalArgumentException("bad");
        var tr = Try.failure(ex);
        assertTrue(tr.isFailure());
        assertEquals(ex, tr.cause());
    }

    @Test
    void mapSuccess() {
        var result = Try.of(() -> 5).map(n -> n * 2);
        assertEquals(10, result.get());
    }

    @Test
    void mapFailurePropagates() {
        var tr = Try.<Integer>of(() -> { throw new RuntimeException("x"); })
                .map(n -> n * 2);
        assertTrue(tr.isFailure());
    }

    @Test
    void mapThrowingBecomesFailure() {
        var tr = Try.of(() -> "hello").map(s -> {
            throw new RuntimeException("map fail");
        });
        assertTrue(tr.isFailure());
        assertEquals("map fail", tr.cause().getMessage());
    }

    @Test
    void flatMapSuccess() {
        var result = Try.of(() -> 3).flatMap(n -> Try.success(n + 1));
        assertEquals(4, result.get());
    }

    @Test
    void flatMapToFailure() {
        var result = Try.of(() -> 3).flatMap(n -> Try.failure(new RuntimeException("no")));
        assertTrue(result.isFailure());
    }

    @Test
    void flatMapOnFailure() {
        var result = Try.<Integer>failure(new RuntimeException("x"))
                .flatMap(n -> Try.success(n + 1));
        assertTrue(result.isFailure());
    }

    @Test
    void recover() {
        var tr = Try.<Integer>of(() -> { throw new RuntimeException("x"); })
                .recover(e -> 0);
        assertEquals(0, tr.get());
    }

    @Test
    void recoverNotCalledOnSuccess() {
        var tr = Try.of(() -> 42).recover(e -> 0);
        assertEquals(42, tr.get());
    }

    @Test
    void getOrElseValue() {
        assertEquals(5, Try.of(() -> 5).getOrElse(0));
        assertEquals(0, Try.<Integer>of(() -> { throw new RuntimeException(); }).getOrElse(0));
    }

    @Test
    void getOrElseSupplier() {
        assertEquals(5, Try.of(() -> 5).getOrElse(() -> 0));
        assertEquals(0, Try.<Integer>of(() -> { throw new RuntimeException(); }).getOrElse(() -> 0));
    }

    @Test
    void ifSuccessCalled() {
        var list = new ArrayList<String>();
        Try.of(() -> "hi").ifSuccess(list::add);
        assertEquals(1, list.size());
        assertEquals("hi", list.getFirst());
    }

    @Test
    void ifSuccessNotCalledOnFailure() {
        var list = new ArrayList<String>();
        Try.<String>of(() -> { throw new RuntimeException(); }).ifSuccess(list::add);
        assertTrue(list.isEmpty());
    }

    @Test
    void ifFailureCalled() {
        var list = new ArrayList<Throwable>();
        Try.of(() -> { throw new RuntimeException("err"); }).ifFailure(list::add);
        assertEquals(1, list.size());
        assertEquals("err", list.getFirst().getMessage());
    }

    @Test
    void ifFailureNotCalledOnSuccess() {
        var list = new ArrayList<Throwable>();
        Try.of(() -> "ok").ifFailure(list::add);
        assertTrue(list.isEmpty());
    }

    @Test
    void foldSuccess() {
        var result = Try.of(() -> 10).fold(e -> "fail", v -> "ok:" + v);
        assertEquals("ok:10", result);
    }

    @Test
    void foldFailure() {
        var result = Try.<Integer>of(() -> { throw new RuntimeException("x"); })
                .fold(Throwable::getMessage, v -> "ok:" + v);
        assertEquals("x", result);
    }

    @Test
    void toEitherSuccess() {
        var either = Try.of(() -> 5).toEither();
        assertTrue(either.isRight());
        assertEquals(5, either.right());
    }

    @Test
    void toEitherFailure() {
        var either = Try.<Integer>of(() -> { throw new RuntimeException("e"); }).toEither();
        assertTrue(either.isLeft());
        assertEquals("e", either.left().getMessage());
    }
}
