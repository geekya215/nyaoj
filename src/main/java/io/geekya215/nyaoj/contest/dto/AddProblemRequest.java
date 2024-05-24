package io.geekya215.nyaoj.contest.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;

public record AddProblemRequest(
        @Positive
        Long problemId,
        @Pattern(regexp = "[A-Z]")
        String sequence, // A ~ Z
        @Range(min = 0, max = 0xFFFFFF)
        Integer color // RGB (0, 0, 0) ~ (255, 255, 255)
) {
}
