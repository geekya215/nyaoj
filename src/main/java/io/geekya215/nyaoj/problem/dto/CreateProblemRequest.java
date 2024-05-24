package io.geekya215.nyaoj.problem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateProblemRequest(
        @NotBlank
        @Size(min = 1, max = 255)
        String title,
        @Positive
        Integer timeLimit,
        @Positive
        Integer memoryLimit
) {
}
