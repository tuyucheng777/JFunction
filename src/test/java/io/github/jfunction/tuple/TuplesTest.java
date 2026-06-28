package io.github.jfunction.tuple;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TuplesTest {

    @Test
    void tuple2Creation() {
        var t = Tuples.of("a", 1);
        assertEquals("a", t._1());
        assertEquals(1, t._2());
    }

    @Test
    void tuple2NullRejected() {
        assertThrows(NullPointerException.class, () -> Tuples.of(null, 1));
        assertThrows(NullPointerException.class, () -> Tuples.of("a", null));
    }

    @Test
    void tuple2Apply() {
        var t = Tuples.of(3, 4);
        assertEquals(7, t.apply(Integer::sum));
    }

    @Test
    void tuple2Swap() {
        var t = Tuples.of("x", 5).swap();
        assertEquals(5, t._1());
        assertEquals("x", t._2());
    }

    @Test
    void tuple2Append() {
        var t3 = Tuples.of("a", "b").append("c");
        assertEquals("a", t3._1());
        assertEquals("b", t3._2());
        assertEquals("c", t3._3());
    }

    @Test
    void tuple2ToEntry() {
        var entry = Tuples.of("key", "value").toEntry();
        assertEquals(Map.entry("key", "value"), entry);
    }

    @Test
    void tuple3Creation() {
        var t = Tuples.of(1, 2, 3);
        assertEquals(1, t._1());
        assertEquals(2, t._2());
        assertEquals(3, t._3());
    }

    @Test
    void tuple3NullRejected() {
        assertThrows(NullPointerException.class, () -> Tuples.of(null, 2, 3));
        assertThrows(NullPointerException.class, () -> Tuples.of(1, null, 3));
        assertThrows(NullPointerException.class, () -> Tuples.of(1, 2, null));
    }

    @Test
    void tuple3Apply() {
        var t = Tuples.of(1, 2, 3);
        assertEquals(6, t.apply((Integer a, Integer b, Integer c) -> a + b + c));
    }

    @Test
    void tuple3Append() {
        var t4 = Tuples.of("a", "b", "c").append("d");
        assertEquals("a", t4._1());
        assertEquals("b", t4._2());
        assertEquals("c", t4._3());
        assertEquals("d", t4._4());
    }

    @Test
    void tuple4Creation() {
        var t = Tuples.of(1, 2, 3, 4);
        assertEquals(1, t._1());
        assertEquals(2, t._2());
        assertEquals(3, t._3());
        assertEquals(4, t._4());
    }

    @Test
    void tuple4NullRejected() {
        assertThrows(NullPointerException.class, () -> Tuples.of(null, 2, 3, 4));
        assertThrows(NullPointerException.class, () -> Tuples.of(1, null, 3, 4));
        assertThrows(NullPointerException.class, () -> Tuples.of(1, 2, null, 4));
        assertThrows(NullPointerException.class, () -> Tuples.of(1, 2, 3, null));
    }

    @Test
    void tuple2Equality() {
        assertEquals(Tuples.of(1, 2), Tuples.of(1, 2));
        assertNotEquals(Tuples.of(1, 2), Tuples.of(1, 3));
    }

    @Test
    void tuple3Equality() {
        assertEquals(Tuples.of(1, 2, 3), Tuples.of(1, 2, 3));
        assertNotEquals(Tuples.of(1, 2, 3), Tuples.of(1, 2, 4));
    }

    @Test
    void tuple4Equality() {
        assertEquals(Tuples.of(1, 2, 3, 4), Tuples.of(1, 2, 3, 4));
        assertNotEquals(Tuples.of(1, 2, 3, 4), Tuples.of(1, 2, 3, 5));
    }
}
