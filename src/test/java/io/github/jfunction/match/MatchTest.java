package io.github.jfunction.match;

import io.github.jfunction.core.Either;
import io.github.jfunction.core.Option;
import io.github.jfunction.core.Try;
import io.github.jfunction.core.Validation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    @Test
    void classifyPositiveInt() {
        assertEquals("positive int: 5", Match.classifyNumber(5));
    }

    @Test
    void classifyNegativeInt() {
        assertEquals("negative int: -1", Match.classifyNumber(-1));
    }

    @Test
    void classifyZero() {
        assertEquals("zero", Match.classifyNumber(0));
    }

    @Test
    void classifyBoolean() {
        assertEquals("boolean: true", Match.classifyNumber(true));
        assertEquals("boolean: false", Match.classifyNumber(false));
    }

    @Test
    void classifyNull() {
        assertEquals("null", Match.classifyNumber(null));
    }

    @Test
    void classifyLongPositive() {
        assertEquals("positive long: 100", Match.classifyNumber(100L));
    }

    @Test
    void classifyLongNegative() {
        assertEquals("negative long: -50", Match.classifyNumber(-50L));
    }

    @Test
    void classifyDoublePositive() {
        assertEquals("positive double: 3.14", Match.classifyNumber(3.14));
    }

    @Test
    void classifyDoubleNegative() {
        assertEquals("negative double: -2.5", Match.classifyNumber(-2.5));
    }

    @Test
    void classifyFloat() {
        assertEquals("float: 1.5", Match.classifyNumber(1.5f));
    }

    @Test
    void classifyOther() {
        assertEquals("other: hello", Match.classifyNumber("hello"));
    }

    @Test
    void rangeLabelNegative() {
        assertEquals("negative", Match.rangeLabel(-10));
    }

    @Test
    void rangeLabelZero() {
        assertEquals("zero", Match.rangeLabel(0));
    }

    @Test
    void rangeLabelSmall() {
        assertEquals("small", Match.rangeLabel(5));
        assertEquals("small", Match.rangeLabel(10));
    }

    @Test
    void rangeLabelMedium() {
        assertEquals("medium", Match.rangeLabel(50));
        assertEquals("medium", Match.rangeLabel(100));
    }

    @Test
    void rangeLabelLarge() {
        assertEquals("large", Match.rangeLabel(200));
    }

    @Test
    void boolLabelIsExhaustive() {
        assertEquals("yes", Match.boolLabel(true));
        assertEquals("no", Match.boolLabel(false));
    }

    @Test
    void narrowToIntSuccess() {
        assertEquals(Option.of(42), Match.narrowToInt(42L));
    }

    @Test
    void narrowToIntFailsOnOverflow() {
        assertTrue(Match.narrowToInt(Long.MAX_VALUE).isEmpty());
        assertTrue(Match.narrowToInt(Long.MIN_VALUE).isEmpty());
    }

    @Test
    void onOption() {
        assertEquals("val:3", Match.onOption(Option.of(3), v -> "val:" + v, () -> "none"));
        assertEquals("none", Match.onOption(Option.none(), v -> "val:" + v, () -> "none"));
    }

    @Test
    void onEither() {
        assertEquals("R:5", Match.onEither(Either.right(5), l -> "L:" + l, r -> "R:" + r));
        assertEquals("L:err", Match.onEither(Either.left("err"), l -> "L:" + l, r -> "R:" + r));
    }

    @Test
    void onTry() {
        assertEquals("ok:10", Match.onTry(Try.success(10), e -> "fail", v -> "ok:" + v));
        assertEquals("boom", Match.onTry(
                Try.failure(new RuntimeException("boom")),
                Throwable::getMessage,
                v -> "ok:" + v));
    }

    @Test
    void onValidation() {
        assertEquals("valid:7", Match.onValidation(
                Validation.valid(7),
                errs -> "invalid:" + errs,
                v -> "valid:" + v));
        assertEquals("invalid:[e1, e2]", Match.onValidation(
                Validation.invalid(List.of("e1", "e2")),
                errs -> "invalid:" + errs,
                v -> "valid:" + v));
    }

    @Test
    void matchValueInt() {
        assertEquals("int:42", Match.matchValue(42,
                i -> "int:" + i, l -> "long:" + l, d -> "double:" + d, s -> "str:" + s, o -> "other"));
    }

    @Test
    void matchValueLong() {
        assertEquals("long:100", Match.matchValue(100L,
                i -> "int:" + i, l -> "long:" + l, d -> "double:" + d, s -> "str:" + s, o -> "other"));
    }

    @Test
    void matchValueDouble() {
        assertEquals("double:3.14", Match.matchValue(3.14,
                i -> "int:" + i, l -> "long:" + l, d -> "double:" + d, s -> "str:" + s, o -> "other"));
    }

    @Test
    void matchValueString() {
        assertEquals("str:hi", Match.matchValue("hi",
                i -> "int:" + i, l -> "long:" + l, d -> "double:" + d, s -> "str:" + s, o -> "other"));
    }

    @Test
    void matchValueNull() {
        assertEquals("other", Match.matchValue(null,
                i -> "int:" + i, l -> "long:" + l, d -> "double:" + d, s -> "str:" + s, o -> "other"));
    }

    @Test
    void matchValueOther() {
        assertEquals("other", Match.matchValue(List.of(1, 2),
                i -> "int:" + i, l -> "long:" + l, d -> "double:" + d, s -> "str:" + s, o -> "other"));
    }
}
