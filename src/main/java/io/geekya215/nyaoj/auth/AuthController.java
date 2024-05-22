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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public @NonNull ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        return switch (authService.login(loginRequest)) {
            case Result.Success(LoginResponse loginResponse) -> ResponseEntity.ok(loginResponse);
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/logout")
    public @NonNull ResponseEntity<?> logout(HttpServletRequest request) {
        final Long userId = (Long) request.getAttribute("userId");
        final Result<Void, ErrorResponse<String>> result = authService.logout(userId);
        return switch (result) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_NO_CONTENT).build();
            case Result.Failure(ErrorResponse(int statusCode, String message)) ->
                    ResponseEntity.status(statusCode).body(message);
        };
    }

    @PostMapping("/signup")
    public @NonNull ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return switch (authService.signUp(signUpRequest)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/refresh_token")
    public @NonNull ResponseEntity<?> refreshToken(HttpServletRequest request) {
        final String refreshToken = request.getHeader("X-Refresh-Token");
        final Result<RefreshAccessTokenResponse, ErrorResponse<String>> result =
                authService.refreshAccessToken(refreshToken);
        return switch (result) {
            case Result.Success(RefreshAccessTokenResponse resp) -> ResponseEntity.ok(resp);
            case Result.Failure(ErrorResponse(int statusCode, String message)) ->
                    ResponseEntity.status(statusCode).body(message);
        };
    }
}
