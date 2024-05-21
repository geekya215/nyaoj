package io.geekya215.nyaoj.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.geekya215.nyaoj.auth.dto.LoginRequest;
import io.geekya215.nyaoj.auth.dto.LoginResponse;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.user.User;
import io.geekya215.nyaoj.user.UserMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtService jwtService;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthService(
            UserMapper userMapper,
            RefreshTokenMapper refreshTokenMapper,
            JwtService jwtService,
            RedisTemplate<String, String> redisTemplate)
    {
        this.userMapper = userMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    public @NonNull Result<LoginResponse, ErrorResponse<String>> login(@NonNull final LoginRequest loginRequest) {
        final QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        final User user = userMapper.selectOne(queryWrapper.eq("username", loginRequest.username()));
        if (user != null && BCrypt.verifyer().verify(loginRequest.password().getBytes(), user.getPassword().getBytes()).verified) {
            if (user.getBan()) {
                return Result.failure(ErrorResponse.of(HttpServletResponse.SC_FORBIDDEN, "user has been banned"));
            }

            final String accessTokenKey = "token:" + user.getId();

            if (redisTemplate.opsForValue().get(accessTokenKey) != null) {
                return Result.failure(ErrorResponse.of(HttpServletResponse.SC_CONFLICT, "user already login"));
            }

            final String accessToken = jwtService.generateToken(user.getId());
            redisTemplate.opsForValue().set(accessTokenKey, accessToken, Duration.ofSeconds(jwtService.getExpirationTime()));

            // remove old refresh token if exist
            final QueryWrapper<RefreshToken> wrapper = new QueryWrapper<>();
            refreshTokenMapper.delete(wrapper.eq("user_id", user.getId()));

            final Instant now = Instant.now();
            final Instant refreshTokenExpireTime = now.plus(15, ChronoUnit.DAYS);
            final String uuid = UUID.randomUUID().toString();
            final RefreshToken refreshToken = new RefreshToken();
            refreshToken.setUserId(user.getId());
            refreshToken.setToken(uuid);
            refreshToken.setExpireAt(refreshTokenExpireTime);

            refreshTokenMapper.insert(refreshToken);

            return Result.success(new LoginResponse(accessToken, uuid, now.plusSeconds(jwtService.getExpirationTime())));
        } else {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_UNAUTHORIZED, "username not match password"));
        }
    }
}
