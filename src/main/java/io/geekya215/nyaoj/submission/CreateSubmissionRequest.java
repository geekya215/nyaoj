package io.geekya215.nyaoj.submission;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSubmissionRequest(
        @NotNull
        String code,
        Language language
) {
}
