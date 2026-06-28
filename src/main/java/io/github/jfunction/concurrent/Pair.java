package io.github.jfunction.concurrent;

import io.github.jfunction.core.Either;
import io.github.jfunction.core.Try;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Joiner;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.function.Function;

/// Parallel execution utilities using Structured Concurrency (JEP 525, preview).
public final class Pair {

    private Pair() {}

    /// Run tasks in parallel and collect all successful results.
    /// Cancels remaining tasks on first failure.
    @SafeVarargs
    public static <T> Try<List<T>> all(Callable<T>... tasks) throws InterruptedException {
        Objects.requireNonNull(tasks, "tasks");
        if (tasks.length == 0) {
            return Try.success(List.of());
        }

        try (var scope = StructuredTaskScope.open(Joiner.<T>allSuccessfulOrThrow())) {
            for (var task : tasks) {
                scope.fork(task);
            }
            List<T> result = scope.join();
            return Try.success(result);
        } catch (StructuredTaskScope.FailedException e) {
            return Try.failure(causeOf(e));
        }
    }

    /// Run tasks in parallel and return the first successful result.
    /// Cancels remaining tasks once one succeeds.
    @SafeVarargs
    public static <T> Try<T> race(Callable<T>... tasks) throws InterruptedException {
        Objects.requireNonNull(tasks, "tasks");
        if (tasks.length == 0) {
            return Try.failure(new IllegalArgumentException("No tasks provided"));
        }

        try (var scope = StructuredTaskScope.open(Joiner.<T>anySuccessfulOrThrow())) {
            for (var task : tasks) {
                scope.fork(task);
            }
            T result = scope.join();
            return Try.success(result);
        } catch (StructuredTaskScope.FailedException e) {
            return Try.failure(causeOf(e));
        }
    }

    /// Map a list of inputs in parallel using structured concurrency.
    public static <A, B> Try<List<B>> mapAll(List<A> inputs, Function<A, B> mapper) throws InterruptedException {
        Objects.requireNonNull(inputs, "inputs");
        Objects.requireNonNull(mapper, "mapper");

        if (inputs.isEmpty()) {
            return Try.success(List.of());
        }

        @SuppressWarnings("unchecked")
        Callable<B>[] tasks = inputs.stream()
                .<Callable<B>>map(a -> () -> mapper.apply(a))
                .toArray(Callable[]::new);

        return all(tasks);
    }

    /// Run tasks in parallel, collecting both successes and failures.
    @SafeVarargs
    public static <T> List<Either<Throwable, T>> gather(Callable<T>... tasks) throws InterruptedException {
        Objects.requireNonNull(tasks, "tasks");
        if (tasks.length == 0) {
            return List.of();
        }

        try (var scope = StructuredTaskScope.open(Joiner.<T>allUntil(_ -> false))) {
            for (var task : tasks) {
                scope.fork(task);
            }

            List<Subtask<T>> subtasks = scope.join();
            var results = new ArrayList<Either<Throwable, T>>(subtasks.size());
            for (Subtask<T> subtask : subtasks) {
                Either<Throwable, T> entry = switch (subtask.state()) {
                    case Subtask.State.SUCCESS -> Either.right(subtask.get());
                    case Subtask.State.FAILED -> Either.left(subtask.exception());
                    case Subtask.State.UNAVAILABLE -> Either.left(
                            new IllegalStateException("Subtask was not executed"));
                };
                results.add(entry);
            }
            return List.copyOf(results);
        }
    }

    private static Throwable causeOf(StructuredTaskScope.FailedException e) {
        Throwable cause = e.getCause();
        return cause != null ? cause : e;
    }
}
