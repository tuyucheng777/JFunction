package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void combineAccumulatesErrors() {
        Validation<String, Integer> v1 = Validation.invalid("e1");
        Validation<String, Integer> v2 = Validation.invalid("e2");

        var combined = Validation.combine(v1, v2, (a, b) -> a + b);
        assertTrue(combined.isInvalid());
        assertEquals(List.of("e1", "e2"), combined.errors());
    }

    @Test
    void combineSuccess() {
        var combined = Validation.combine(
                Validation.valid(2),
                Validation.valid(3),
                (a, b) -> a + b
        );
        assertTrue(combined.isValid());
        assertEquals(5, combined.get());
    }
}
