package io.github.jfunction.match;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PredicatesTest {

    @Test
    void isPositive() {
        assertTrue(Predicates.isPositive().test(5));
        assertFalse(Predicates.isPositive().test(0));
        assertFalse(Predicates.isPositive().test(-1));
    }

    @Test
    void isNegative() {
        assertTrue(Predicates.isNegative().test(-3));
        assertFalse(Predicates.isNegative().test(0));
        assertFalse(Predicates.isNegative().test(1));
    }

    @Test
    void inRange() {
        var pred = Predicates.inRange(1, 10);
        assertTrue(pred.test(1));
        assertTrue(pred.test(5));
        assertTrue(pred.test(10));
        assertFalse(pred.test(0));
        assertFalse(pred.test(11));
    }

    @Test
    void notNull() {
        var pred = Predicates.<String>notNull();
        assertTrue(pred.test("hello"));
        assertFalse(pred.test(null));
    }

    @Test
    void isEqual() {
        var pred = Predicates.isEqual("hello");
        assertTrue(pred.test("hello"));
        assertFalse(pred.test("world"));
        assertFalse(pred.test(null));
    }

    @Test
    void isEqualWithNull() {
        var pred = Predicates.isEqual(null);
        assertTrue(pred.test(null));
        assertFalse(pred.test("x"));
    }

    @Test
    void and() {
        var pred = Predicates.and(Predicates.isPositive(), Predicates.inRange(1, 5));
        assertTrue(pred.test(3));
        assertFalse(pred.test(7));
        assertFalse(pred.test(-1));
    }

    @Test
    void or() {
        var pred = Predicates.or(Predicates.isNegative(), Predicates.isEqual(0));
        assertTrue(pred.test(-5));
        assertTrue(pred.test(0));
        assertFalse(pred.test(1));
    }
}
