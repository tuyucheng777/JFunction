package io.github.jfunction.match;

import io.github.jfunction.core.Option;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    @Test
    void classifyNumber() {
        assertEquals("zero", Match.classifyNumber(0));
        assertEquals("positive int: 5", Match.classifyNumber(5));
        assertEquals("negative int: -1", Match.classifyNumber(-1));
        assertEquals("boolean: true", Match.classifyNumber(true));
        assertEquals("null", Match.classifyNumber(null));
    }

    @Test
    void rangeLabel() {
        assertEquals("small", Match.rangeLabel(5));
        assertEquals("medium", Match.rangeLabel(50));
        assertEquals("large", Match.rangeLabel(200));
    }

    @Test
    void boolLabelIsExhaustive() {
        assertEquals("yes", Match.boolLabel(true));
        assertEquals("no", Match.boolLabel(false));
    }

    @Test
    void narrowToInt() {
        assertEquals(Option.of(42), Match.narrowToInt(42L));
        assertTrue(Match.narrowToInt(Long.MAX_VALUE).isEmpty());
    }
}
