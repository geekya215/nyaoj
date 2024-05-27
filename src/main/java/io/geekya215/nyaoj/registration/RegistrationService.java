package io.geekya215.nyaoj.registration;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.contest.Contest;
import io.geekya215.nyaoj.contest.ContestMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
public class RegistrationService {
    private final RegistrationMapper registrationMapper;
    private final ContestMapper contestMapper;

    public RegistrationService(RegistrationMapper registrationMapper,
                               ContestMapper contestMapper) {
        this.registrationMapper = registrationMapper;
        this.contestMapper = contestMapper;
    }

    public @NonNull Result<Void, ErrorResponse<String>>
    createRegistration(@NonNull final Long userId,
                       @NonNull final Long contestId) {

        final QueryWrapper<Contest> contestQueryWrapper = new QueryWrapper<>();
        final Contest contest = contestMapper.selectOne(contestQueryWrapper.eq("id", contestId));
        if (contest == null) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such contest"));
        }

        final LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(contest.getStartTime())) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_BAD_REQUEST, "contest already started"));
        }

        final QueryWrapper<Registration> registrationQueryWrapper = new QueryWrapper<>();
        if (registrationMapper.exists(registrationQueryWrapper.eq("user_id", userId).eq("contest_id", contestId))) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_CONFLICT, "user already register this contest"));
        }

        final Registration registration = new Registration();
        registration.setUserId(userId);
        registration.setContestId(contestId);
        registrationMapper.insert(registration);

        return Result.success();
    }
}
