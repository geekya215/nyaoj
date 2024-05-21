package io.geekya215.nyaoj.auth.dto;

import org.springframework.lang.NonNull;

import java.time.Instant;

public record LoginResponse(
        @NonNull String accessToken,
        @NonNull String refreshToken,
        @NonNull Instant expireAt
) {
}
