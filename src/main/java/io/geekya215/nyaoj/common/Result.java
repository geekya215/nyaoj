package io.geekya215.nyaoj.common;

import org.springframework.lang.Nullable;

public sealed interface Result<T, E> permits Result.Success, Result.Failure  {
    static <E> Success<Void, E> success() {
        return new Success<>(null);
    }

    static <T, E> Success<T, E> success(T value) {
        return new Success<>(value);
    }

    static <T> Failure<T, Void> failure() {
        return new Failure<>(null);
    }

    static <T, E> Failure<T, E> failure(E error) {
        return new Failure<>(error);
    }

    record Success<T, E>(@Nullable T value) implements Result<T, E> {
    }

    record Failure<T, E>(@Nullable E error) implements Result<T, E> {
    }
}
