package io.github.jfunction.core;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    @Test
    void validCreation() {
        var v = Validation.valid(42);
        assertTrue(v.isValid());
        assertFalse(v.isInvalid());
        assertEquals(42, v.get());
    }

    @Test
    void invalidSingleError() {
        var v = Validation.<String, Integer>invalid("bad");
        assertTrue(v.isInvalid());
        assertFalse(v.isValid());
        assertEquals(List.of("bad"), v.errors());
    }

    @Test
    void invalidMultipleErrors() {
        var v = Validation.<String, Integer>invalid("e1", "e2", "e3");
        assertEquals(List.of("e1", "e2", "e3"), v.errors());
    }

    @Test
    void invalidListErrors() {
        var v = Validation.<String, Integer>invalid(List.of("a", "b"));
        assertEquals(List.of("a", "b"), v.errors());
    }

    @Test
    void invalidEmptyListThrows() {
        assertThrows(IllegalArgumentException.class, () -> Validation.invalid(List.of()));
    }

    @Test
    void getOnInvalidThrows() {
        var v = Validation.<String, Integer>invalid("err");
        assertThrows(IllegalStateException.class, v::get);
    }

    @Test
    void errorsOnValidReturnsEmpty() {
        var v = Validation.valid(1);
        assertTrue(v.errors().isEmpty());
    }

    @Test
    void mapValid() {
        var result = Validation.<String, Integer>valid(5).map(n -> n * 2);
        assertTrue(result.isValid());
        assertEquals(10, result.get());
    }

    @Test
    void mapInvalid() {
        var result = Validation.<String, Integer>invalid("err").map(n -> n * 2);
        assertTrue(result.isInvalid());
        assertEquals(List.of("err"), result.errors());
    }

    @Test
    void flatMapValid() {
        var result = Validation.<String, Integer>valid(5)
                .flatMap(n -> Validation.valid(n + 1));
        assertEquals(6, result.get());
    }

    @Test
    void flatMapValidToInvalid() {
        var result = Validation.<String, Integer>valid(5)
                .flatMap(n -> Validation.invalid("nope"));
        assertTrue(result.isInvalid());
    }

    @Test
    void flatMapOnInvalid() {
        var result = Validation.<String, Integer>invalid("err")
                .flatMap(n -> Validation.valid(n + 1));
        assertTrue(result.isInvalid());
    }

    @Test
    void orElseOnValid() {
        var v = Validation.<String, Integer>valid(1);
        var alt = Validation.<String, Integer>valid(99);
        assertEquals(1, v.orElse(alt).get());
    }

    @Test
    void orElseOnInvalid() {
        var v = Validation.<String, Integer>invalid("err");
        var alt = Validation.<String, Integer>valid(99);
        assertEquals(99, v.orElse(alt).get());
    }

    @Test
    void getOrElseValue() {
        assertEquals(5, Validation.valid(5).getOrElse(0));
        assertEquals(0, Validation.<String, Integer>invalid("e").getOrElse(0));
    }

    @Test
    void getOrElseSupplier() {
        assertEquals(5, Validation.valid(5).getOrElse(() -> 0));
        assertEquals(0, Validation.<String, Integer>invalid("e").getOrElse(() -> 0));
    }

    @Test
    void combineSuccess() {
        var combined = Validation.combine(
                Validation.valid(2),
                Validation.valid(3),
                Integer::sum
        );
        assertTrue(combined.isValid());
        assertEquals(5, combined.get());
    }

    @Test
    void combineAccumulatesErrors() {
        Validation<String, Integer> v1 = Validation.invalid("e1");
        Validation<String, Integer> v2 = Validation.invalid("e2");

        var combined = Validation.combine(v1, v2, Integer::sum);
        assertTrue(combined.isInvalid());
        assertEquals(List.of("e1", "e2"), combined.errors());
    }

    @Test
    void combineLeftInvalidRightValid() {
        Validation<String, Integer> v1 = Validation.invalid("e1");
        Validation<String, Integer> v2 = Validation.valid(10);

        var combined = Validation.combine(v1, v2, Integer::sum);
        assertTrue(combined.isInvalid());
        assertEquals(List.of("e1"), combined.errors());
    }

    @Test
    void combineLeftValidRightInvalid() {
        Validation<String, Integer> v1 = Validation.valid(10);
        Validation<String, Integer> v2 = Validation.invalid("e2");

        var combined = Validation.combine(v1, v2, Integer::sum);
        assertTrue(combined.isInvalid());
        assertEquals(List.of("e2"), combined.errors());
    }

    @Test
    void toEitherValid() {
        var either = Validation.valid(5).toEither();
        assertTrue(either.isRight());
        assertEquals(5, either.right());
    }

    @Test
    void toEitherInvalid() {
        var either = Validation.<String, Integer>invalid("e1", "e2").toEither();
        assertTrue(either.isLeft());
        assertEquals(List.of("e1", "e2"), either.left());
    }
}
