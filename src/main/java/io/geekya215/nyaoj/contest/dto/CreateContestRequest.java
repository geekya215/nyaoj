package io.geekya215.nyaoj.contest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateContestRequest(
        @NotBlank
        @Size(min = 1, max = 255)
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
