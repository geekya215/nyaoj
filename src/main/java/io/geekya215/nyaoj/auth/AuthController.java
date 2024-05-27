package io.geekya215.nyaoj.auth;

import io.geekya215.nyaoj.auth.dto.LoginRequest;
import io.geekya215.nyaoj.auth.dto.LoginResponse;
import io.geekya215.nyaoj.auth.dto.RefreshAccessTokenResponse;
import io.geekya215.nyaoj.auth.dto.SignUpRequest;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public @NonNull ResponseEntity<?> login(@RequestBody @Valid final LoginRequest loginRequest) {
        return switch (authService.login(loginRequest)) {
            case Result.Success(LoginResponse loginResponse) -> ResponseEntity.ok(loginResponse);
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/logout")
    public @NonNull ResponseEntity<?> logout(final HttpServletRequest request) {
        final Long userId = (Long) request.getAttribute("userId");

        return switch (authService.logout(userId)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_NO_CONTENT).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/signup")
    public @NonNull ResponseEntity<?> signUp(@RequestBody @Valid final SignUpRequest signUpRequest) {
        return switch (authService.signUp(signUpRequest)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/refresh_token")
    public @NonNull ResponseEntity<?> refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken) {

        return switch (authService.refreshAccessToken(refreshToken)) {
            case Result.Success(RefreshAccessTokenResponse resp) -> ResponseEntity.ok(resp);
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }
}
