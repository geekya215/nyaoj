package io.geekya215.nyaoj.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration-time}")
    private Long expirationTime;
    @Value("${jwt.issuer}")
    private String issuer;

    public @NonNull Long getExpirationTime() {
        return expirationTime;
    }

    public @NonNull String generateToken(@NonNull Long userId) {
        final Instant now = Instant.now();
        return JWT.create()
                .withClaim("user_id", userId)
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(now.plusSeconds(expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }
}
