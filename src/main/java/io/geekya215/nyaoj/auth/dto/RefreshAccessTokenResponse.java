package io.geekya215.nyaoj.auth.dto;

import org.springframework.lang.NonNull;

import java.time.Instant;

public record RefreshAccessTokenResponse(
        @NonNull String accessToken,
        @NonNull Instant expireAt
) {
}
