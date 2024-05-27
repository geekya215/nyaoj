package io.geekya215.nyaoj.submission;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.contest.Contest;
import io.geekya215.nyaoj.contest.ContestMapper;
import io.geekya215.nyaoj.problem.entity.ContestProblem;
import io.geekya215.nyaoj.problem.mapper.ContestProblemMapper;
import io.geekya215.nyaoj.registration.Registration;
import io.geekya215.nyaoj.registration.RegistrationMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SubmissionService {
    private final SubmissionMapper submissionMapper;
    private final RegistrationMapper registrationMapper;
    private final ContestProblemMapper contestProblemMapper;
    private final ContestMapper contestMapper;

    public SubmissionService(SubmissionMapper submissionMapper,
                             RegistrationMapper registrationMapper,
                             ContestProblemMapper contestProblemMapper,
                             ContestMapper contestMapper) {
        this.submissionMapper = submissionMapper;
        this.registrationMapper = registrationMapper;
        this.contestProblemMapper = contestProblemMapper;
        this.contestMapper = contestMapper;
    }

    public @NonNull Result<Void, ErrorResponse<String>> createSubmission(
            @NonNull final Long userId,
            @NonNull final Long contestId,
            @NonNull final Integer sequence,
            @NonNull final CreateSubmissionRequest createSubmissionRequest) {

        final QueryWrapper<Registration>  registrationQueryWrapper = new QueryWrapper<>();
        if (!registrationMapper.exists(registrationQueryWrapper.eq("user_id", userId).eq("contest_id", contestId))) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_FORBIDDEN, "no permission to submit"));
        }

        final QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
        final ContestProblem contestProblem = contestProblemMapper.selectOne(
                contestProblemQueryWrapper
                        .eq("contest_id", contestId)
                        .eq("sequence", sequence));

        if (contestProblem == null) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such contest problem"));
        }

        QueryWrapper<Contest> contestQueryWrapper = new QueryWrapper<>();
        // maybe should check contest exist?
        final Contest contest = contestMapper.selectOne(contestQueryWrapper.eq("id", contestId));

        final LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(contest.getEndTime())) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_BAD_REQUEST, "contest already ended"));
        }

        final Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setContestProblemId(contestProblem.getId());
        submission.setCode(createSubmissionRequest.code());
        submission.setLanguage(createSubmissionRequest.language());
        submission.setVerdict(Verdict.WAITING);

        submissionMapper.insert(submission);

        return Result.success();
    }
}
