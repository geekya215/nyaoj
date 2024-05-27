package io.geekya215.nyaoj.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.geekya215.nyaoj.auth.dto.LoginRequest;
import io.geekya215.nyaoj.auth.dto.LoginResponse;
import io.geekya215.nyaoj.auth.dto.RefreshAccessTokenResponse;
import io.geekya215.nyaoj.auth.dto.SignUpRequest;
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

    public AuthService(UserMapper userMapper,
                       RefreshTokenMapper refreshTokenMapper,
                       JwtService jwtService,
                       RedisTemplate<String, String> redisTemplate) {
        this.userMapper = userMapper;
        this.refreshTokenMapper = refreshTokenMapper;
        this.jwtService = jwtService;
        this.redisTemplate = redisTemplate;
    }

    static @NonNull String buildRedisKey(@NonNull Long userId) {
        return "token:" + userId;
    }

    public @NonNull Result<LoginResponse, ErrorResponse<String>> login(@NonNull final LoginRequest loginRequest) {
        final QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        final User user = userMapper.selectOne(queryWrapper.eq("username", loginRequest.username()));
        if (user != null && BCrypt.verifyer().verify(loginRequest.password().getBytes(), user.getPassword().getBytes()).verified) {
            if (user.getBan()) {
                return Result.failure(ErrorResponse.of(HttpServletResponse.SC_FORBIDDEN, "user has been banned"));
            }

            final String accessTokenKey = buildRedisKey(user.getId());

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

    public @NonNull Result<Void, ErrorResponse<String>> logout(final Long userId) {
        redisTemplate.delete(buildRedisKey(userId));

        final QueryWrapper<RefreshToken> wrapper = new QueryWrapper<>();
        refreshTokenMapper.delete(wrapper.eq("user_id", userId));

        return Result.success();
    }

    public @NonNull Result<Void, ErrorResponse<String>> signUp(@NonNull final SignUpRequest signUpRequest) {
        final QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        final User user = userMapper.selectOne(queryWrapper
                .eq("username", signUpRequest.username())
                .eq("email", signUpRequest.email()));

        if (user != null) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_CONFLICT, "username or email already exist"));
        } else {
            final String hashedPassword = BCrypt.withDefaults().hashToString(4, signUpRequest.password().toCharArray());

            final User newUser = new User();
            newUser.setUsername(signUpRequest.username());
            newUser.setEmail(signUpRequest.email());
            newUser.setPassword(hashedPassword);

            userMapper.insert(newUser);

            return Result.success();
        }
    }

    public @NonNull Result<RefreshAccessTokenResponse, ErrorResponse<String>> refreshAccessToken(@NonNull String token) {
        final QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
        final RefreshToken refreshToken = refreshTokenMapper.selectOne(queryWrapper.eq("token", token));
        if (refreshToken != null) {
            final Instant now = Instant.now();
            if (now.isBefore(refreshToken.getExpireAt())) {
                final String accessToken = jwtService.generateToken(refreshToken.getUserId());

                redisTemplate.opsForValue().set(buildRedisKey(refreshToken.getUserId()), accessToken, Duration.ofSeconds(jwtService.getExpirationTime()));

                final RefreshAccessTokenResponse refreshAccessTokenResponse =
                        new RefreshAccessTokenResponse(accessToken, now.plusSeconds(jwtService.getExpirationTime()));
                return Result.success(refreshAccessTokenResponse);
            } else {
                // remove out of date refresh token
                refreshTokenMapper.delete(queryWrapper.eq("token", token));
                return Result.failure(ErrorResponse.of(HttpServletResponse.SC_UNAUTHORIZED, "out of date refresh token"));
            }
        } else {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_UNAUTHORIZED, "invalid refresh token"));
        }
    }
}
