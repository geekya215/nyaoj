package io.geekya215.nyaoj.contest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.contest.dto.AddProblemRequest;
import io.geekya215.nyaoj.contest.dto.CreateContestRequest;
import io.geekya215.nyaoj.contest.dto.SingleContestResponse;
import io.geekya215.nyaoj.problem.entity.ContestProblem;
import io.geekya215.nyaoj.problem.entity.Problem;
import io.geekya215.nyaoj.problem.entity.ProblemFile;
import io.geekya215.nyaoj.problem.mapper.ContestProblemMapper;
import io.geekya215.nyaoj.problem.mapper.ProblemFileMapper;
import io.geekya215.nyaoj.problem.mapper.ProblemMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestService {
    private final ContestMapper contestMapper;
    private final ProblemMapper problemMapper;
    private final ProblemFileMapper problemFileMapper;
    private final ContestProblemMapper contestProblemMapper;

    public ContestService(
            ContestMapper contestMapper,
            ProblemMapper problemMapper,
            ProblemFileMapper problemFileMapper,
            ContestProblemMapper contestProblemMapper
    ) {
        this.contestMapper = contestMapper;
        this.problemMapper = problemMapper;
        this.problemFileMapper = problemFileMapper;
        this.contestProblemMapper = contestProblemMapper;
    }

    public @NonNull Result<Void, ErrorResponse<String>> createContest(@NonNull final CreateContestRequest createContestRequest) {
        if (createContestRequest.startTime().isAfter(createContestRequest.endTime())) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_BAD_REQUEST, "contest start time must before end time"));
        }

        final Contest contest = new Contest();
        contest.setTitle(createContestRequest.title());
        contest.setDescription(createContestRequest.description());
        contestMapper.insert(contest);
        return Result.success();
    }

    public @NonNull Result<SingleContestResponse, ErrorResponse<String>> getContestById(@NonNull Long contestId) {
        final Contest contest = contestMapper.selectById(contestId);
        if (contest != null) {
            return Result.success(new SingleContestResponse(
                    contest.getTitle(), contest.getDescription(), contest.getStartTime(), contest.getEndTime()));
        } else {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such contest"));
        }
    }

    public @NonNull Result<List<SingleContestResponse>, ErrorResponse<String>> getContestList(
            @NonNull Integer page,
            @NonNull Integer size
    ) {
        final Page<Contest> p = new Page<>(page, size);
        final Page<Contest> contests = contestMapper.selectPage(p, null);
        final List<SingleContestResponse> result =
                contests.getRecords().stream().map(r -> new SingleContestResponse(r.getTitle(), r.getDescription(), r.getStartTime(), r.getEndTime())).toList();
        return Result.success(result);
    }

    public @NonNull Result<Void, ErrorResponse<String>> addContestProblem(
            @NonNull final Long contestId,
            @NonNull final AddProblemRequest addProblemRequest
    ) {
        final QueryWrapper<Contest> contestWrapper = new QueryWrapper<>();
        if (!contestMapper.exists(contestWrapper.eq("id", contestId))) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such contest"));
        }

        final QueryWrapper<Problem> problemWrapper = new QueryWrapper<>();
        if (!problemMapper.exists(problemWrapper.eq("id", addProblemRequest.problemId()))) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such problem"));
        }

        final QueryWrapper<ContestProblem> contestProblemWrapper = new QueryWrapper<>();
        final Long cnt = contestProblemMapper.selectCount(contestProblemWrapper
                .eq("contest_id", contestId)
                .eq("problem_id", addProblemRequest.problemId()));
        if (cnt > 0) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_CONFLICT, "contest problem already exist"));
        }

        final QueryWrapper<ProblemFile> problemFileWrapper = new QueryWrapper<>();
        final ProblemFile problemFile = problemFileMapper.selectOne(problemFileWrapper.eq("problem_id", addProblemRequest.problemId()));
        if (problemFile == null
                || problemFile.getStatementUuid() == null
                || problemFile.getSampleUuid() == null
                || problemFile.getTestcaseUuid() == null) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_BAD_REQUEST, "problem file not valid"));
        }

        final ContestProblem contestProblem = new ContestProblem();

        contestProblem.setContestId(contestId);
        contestProblem.setProblemId(addProblemRequest.problemId());
        contestProblem.setSequence(addProblemRequest.sequence().charAt(0));
        contestProblem.setColor(addProblemRequest.color());
        contestProblemMapper.insert(contestProblem);

        return Result.success();
    }
}
