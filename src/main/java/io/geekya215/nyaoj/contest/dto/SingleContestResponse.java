package io.geekya215.nyaoj.contest.dto;

import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public record SingleContestResponse(
        @NonNull String title,
        @NonNull String description,
        @NonNull LocalDateTime startTime,
        @NonNull LocalDateTime endTime
) {
}
