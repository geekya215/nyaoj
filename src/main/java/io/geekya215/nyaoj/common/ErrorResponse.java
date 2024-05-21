package io.geekya215.nyaoj.common;

public record ErrorResponse<T>(int statusCode, T body) {
    public static <T> ErrorResponse<T> of(int statusCode, T body) {
        return new ErrorResponse<>(statusCode, body);
    }
}
