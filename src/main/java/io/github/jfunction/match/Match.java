package io.github.jfunction.match;

import io.github.jfunction.core.Either;
import io.github.jfunction.core.Option;
import io.github.jfunction.core.Try;
import io.github.jfunction.core.Validation;

import java.util.function.Function;
import java.util.function.Supplier;

/// Pattern-matching helpers leveraging Java 26 primitive type patterns (JEP 530).
public final class Match {

    private Match() {}

    /// Classify a numeric value using primitive patterns and guards.
    public static String classifyNumber(Object value) {
        return switch (value) {
            case int i when i < 0     -> "negative int: " + i;
            case int i when i == 0    -> "zero";
            case int i                -> "positive int: " + i;
            case long l when l < 0    -> "negative long: " + l;
            case long l               -> "positive long: " + l;
            case double d when d < 0  -> "negative double: " + d;
            case float f              -> "float: " + f;
            case double d             -> "positive double: " + d;
            case boolean b            -> "boolean: " + b;
            case null                 -> "null";
            default                   -> "other: " + value;
        };
    }

    /// Match on a range using primitive int patterns with guards.
    public static String rangeLabel(int value) {
        return switch (value) {
            case int i when i < 0           -> "negative";
            case int i when i == 0          -> "zero";
            case int i when i <= 10         -> "small";
            case int i when i <= 100        -> "medium";
            case int _                      -> "large";
        };
    }

    /// Exhaustive boolean match — no default needed.
    public static String boolLabel(boolean value) {
        return switch (value) {
            case true  -> "yes";
            case false -> "no";
        };
    }

    /// Safe narrowing: match a `long` as `int` only when lossless.
    public static Option<Integer> narrowToInt(long value) {
        return switch (value) {
            case int i -> Option.of(i);
            default    -> Option.none();
        };
    }

    /// Pattern-match on [Option].
    public static <T, R> R onOption(Option<T> option,
                                     Function<? super T, ? extends R> onSome,
                                     Supplier<? extends R> onNone) {
        return option.match(onSome, onNone);
    }

    /// Pattern-match on [Either].
    public static <L, R, T> T onEither(Either<L, R> either,
                                          Function<? super L, ? extends T> onLeft,
                                          Function<? super R, ? extends T> onRight) {
        return either.fold(onLeft, onRight);
    }

    /// Pattern-match on [Try].
    public static <T, R> R onTry(Try<T> tr,
                                  Function<? super Throwable, ? extends R> onFailure,
                                  Function<? super T, ? extends R> onSuccess) {
        return tr.fold(onFailure, onSuccess);
    }

    /// Pattern-match on [Validation].
    public static <E, T, R> R onValidation(Validation<E, T> validation,
                                            Function<? super java.util.List<E>, ? extends R> onInvalid,
                                            Function<? super T, ? extends R> onValid) {
        return switch (validation) {
            case Validation.Valid(var v)   -> onValid.apply(v);
            case Validation.Invalid(var e) -> onInvalid.apply(e);
        };
    }

    /// Match any object against common numeric/boxed types.
    public static <R> R matchValue(Object value,
                                      Function<Integer, R> onInt,
                                      Function<Long, R> onLong,
                                      Function<Double, R> onDouble,
                                      Function<String, R> onString,
                                      Function<Object, R> onOther) {
        return switch (value) {
            case Integer i  -> onInt.apply(i);
            case Long l     -> onLong.apply(l);
            case Double d   -> onDouble.apply(d);
            case String s   -> onString.apply(s);
            case null       -> onOther.apply(null);
            default         -> onOther.apply(value);
        };
    }
}
