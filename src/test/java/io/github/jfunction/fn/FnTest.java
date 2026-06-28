package io.github.jfunction.fn;

import io.github.jfunction.core.Either;
import io.github.jfunction.core.Option;
import io.github.jfunction.core.Try;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FnTest {

    @Test
    void pipeComposesLeftToRight() {
        var fn = Fn.pipe(
                (Integer n) -> n + 1,
                (Integer n) -> n * 2
        );
        assertEquals(8, fn.apply(3));
    }

    @Test
    void pipeEmptyIsIdentity() {
        @SuppressWarnings("unchecked")
        var fn = Fn.<Integer>pipe();
        assertEquals(5, fn.apply(5));
    }

    @Test
    void pipeSingleStage() {
        var fn = Fn.pipe((Integer n) -> n + 10);
        assertEquals(15, fn.apply(5));
    }

    @Test
    void composeRightToLeft() {
        var fn = Fn.compose(
                (Integer n) -> n * 2,
                (Integer n) -> n + 1
        );
        assertEquals(8, fn.apply(3));
    }

    @Test
    void andThenLeftToRight() {
        var fn = Fn.andThen(
                (Integer n) -> n + 1,
                (Integer n) -> n * 2
        );
        assertEquals(8, fn.apply(3));
    }

    @Test
    void curryAndUncurry() {
        var curried = Fn.curry((Integer a, Integer b) -> a + b);
        assertEquals(7, curried.apply(3).apply(4));

        var uncurried = Fn.uncurry(curried);
        assertEquals(7, uncurried.apply(3, 4));
    }

    @Test
    void constant() {
        var fn = Fn.<String, Integer>constant(42);
        assertEquals(42, fn.apply("anything"));
        assertEquals(42, fn.apply("something else"));
    }

    @Test
    void identity() {
        var fn = Fn.<String>identity();
        assertEquals("hello", fn.apply("hello"));
    }

    @Test
    void notNegatesPredicate() {
        var isPositive = Fn.not((Integer n) -> n < 0);
        assertTrue(isPositive.test(5));
        assertFalse(isPositive.test(-1));
    }

    @Test
    void liftOption() {
        var lifted = Fn.liftOption((Integer n) -> n * 2);
        assertEquals(Option.of(10), lifted.apply(Option.of(5)));
        assertTrue(lifted.apply(Option.none()).isEmpty());
    }

    @Test
    void liftEither() {
        var lifted = Fn.<String, Integer, Integer>liftEither(n -> n * 3);
        var right = Either.<String, Integer>right(4);
        var left = Either.<String, Integer>left("err");

        assertEquals(12, lifted.apply(right).right());
        assertTrue(lifted.apply(left).isLeft());
        assertEquals("err", lifted.apply(left).left());
    }

    @Test
    void liftTry() {
        var lifted = Fn.liftTry((Integer n) -> n + 1);
        var success = Try.success(9);
        var failure = Try.<Integer>failure(new RuntimeException("x"));

        assertEquals(10, lifted.apply(success).get());
        assertTrue(lifted.apply(failure).isFailure());
    }

    @Test
    void tap() {
        var log = new ArrayList<Integer>();
        var fn = Fn.tap((Integer n) -> log.add(n));

        assertEquals(5, fn.apply(5));
        assertEquals(1, log.size());
        assertEquals(5, log.getFirst());
    }

    @Test
    void memoize() {
        var counter = new int[]{0};
        var memo = Fn.memoize(() -> {
            counter[0]++;
            return "computed";
        });

        assertEquals("computed", memo.get());
        assertEquals("computed", memo.get());
        assertEquals(1, counter[0]);
    }

    @Test
    void partial() {
        var add = Fn.partial((Integer a, Integer b) -> a + b, 10);
        assertEquals(15, add.apply(5));
        assertEquals(20, add.apply(10));
    }
}
