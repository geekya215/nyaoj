package io.geekya215.nyaoj.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final RedisTemplate<String, String> redisTemplate;

    public AuthInterceptor(JwtService jwtService, RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean pass = false;

        if (request.getMethod().equals("GET")) {
            // ignore /contests path
            if (request.getRequestURI().equals("/contests")) {
                return true;
            }
        }

        final String authentication = request.getHeader("Authorization");
        if (authentication != null && authentication.startsWith("Bearer ")) {
            final String token = authentication.substring(7);
            try {
                // Todo
                // use JwtService
                final DecodedJWT decode = JWT.decode(token);
                final Long userId = decode.getClaim("user_id").asLong();

                // search in redis
                final String tmp = redisTemplate.opsForValue().get("token:" + userId);
                if (tmp != null) {
                    request.setAttribute("userId", userId);
                    pass = true;
                }
            } catch (JWTDecodeException e) {
                // ignore
            }
        }

        if (!pass) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        return pass;
    }
}
