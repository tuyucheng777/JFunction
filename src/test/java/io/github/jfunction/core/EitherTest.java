package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EitherTest {

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
    void fold() {
        assertEquals("ok:3", Either.<String, Integer>right(3).fold(l -> "fail:" + l, r -> "ok:" + r));
        assertEquals("fail:x", Either.<String, Integer>left("x").fold(l -> "fail:" + l, r -> "ok:" + r));
    }
}
