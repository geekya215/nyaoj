package io.geekya215.nyaoj.auth;

import io.geekya215.nyaoj.auth.dto.LoginRequest;
import io.geekya215.nyaoj.auth.dto.LoginResponse;
import io.geekya215.nyaoj.auth.dto.SignUpRequest;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest) {
        return switch (authService.login(loginRequest)) {
            case Result.Success(LoginResponse loginResponse) -> ResponseEntity.ok(loginResponse);
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return switch (authService.signUp(signUpRequest)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }
}
