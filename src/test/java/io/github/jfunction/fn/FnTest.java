package io.github.jfunction.fn;

import io.github.jfunction.core.Option;
import org.junit.jupiter.api.Test;

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
    void curryAndUncurry() {
        var curried = Fn.curry((Integer a, Integer b) -> a + b);
        var fn = Fn.uncurry(curried);
        assertEquals(7, fn.apply(3, 4));
    }

    @Test
    void liftOption() {
        var lifted = Fn.liftOption(String::valueOf);
        assertEquals(Option.of("42"), lifted.apply(Option.of(42)));
        assertTrue(lifted.apply(Option.none()).isEmpty());
    }
}
