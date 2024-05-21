package io.geekya215.nyaoj.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank
        @Size(min = 4, max = 32)
        String username,

        @Email
        String email,

        @NotBlank
        @Size(min = 6, max = 32)
        String password
) {
}
