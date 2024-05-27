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
import io.geekya215.nyaoj.storage.MinioService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContestService {
    private final ContestMapper contestMapper;
    private final ProblemMapper problemMapper;
    private final ProblemFileMapper problemFileMapper;
    private final ContestProblemMapper contestProblemMapper;
    private final MinioService minioService;

    public ContestService(ContestMapper contestMapper,
                          ProblemMapper problemMapper,
                          ProblemFileMapper problemFileMapper,
                          ContestProblemMapper contestProblemMapper,
                          MinioService minioService) {
        this.contestMapper = contestMapper;
        this.problemMapper = problemMapper;
        this.problemFileMapper = problemFileMapper;
        this.contestProblemMapper = contestProblemMapper;
        this.minioService = minioService;
    }

    // call after check contest already exist
    public boolean isAfterContestStarted(@NonNull Long contestId) {
        final Contest contest = contestMapper.selectById(contestId);
        final LocalDateTime now = LocalDateTime.now();

        return now.isAfter(contest.getStartTime());
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

        // each problem can only be assigned once
        final QueryWrapper<ContestProblem> existContestProblemWrapper = new QueryWrapper<>();
        if (contestProblemMapper.exists(existContestProblemWrapper
                .eq("problem_id", addProblemRequest.problemId()))) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_CONFLICT, "contest problem already exist"));
        }

        final QueryWrapper<ContestProblem> duplicateSequenceContestProblemWrapper = new QueryWrapper<>();
        if (contestProblemMapper.exists(duplicateSequenceContestProblemWrapper
                .eq("contest_id", contestId)
                .eq("sequence", addProblemRequest.sequence()))) {

            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_CONFLICT, "contest problem sequence already exist"));
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
        contestProblem.setSequence(addProblemRequest.sequence());
        contestProblem.setColor(addProblemRequest.color());
        contestProblemMapper.insert(contestProblem);

        return Result.success();
    }

    public @NonNull Result<Void, ErrorResponse<String>> getContestProblemStatement(
            @NonNull final Long contestId,
            @NonNull final Integer problemSequence,
            @NonNull final HttpServletResponse response
    ) {
        final QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
        final ContestProblem contestProblem = contestProblemMapper
                .selectOne(contestProblemQueryWrapper.eq("contest_id", contestId).eq("sequence", problemSequence));
        if (contestProblem == null) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such contest problem"));
        }

        if (!isAfterContestStarted(contestId)) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_FORBIDDEN, "Contest has not started yet"));
        }

        final QueryWrapper<ProblemFile> problemFileQueryWrapper = new QueryWrapper<>();
        final ProblemFile problemFile = problemFileMapper
                .selectOne(problemFileQueryWrapper.eq("problem_id", contestProblem.getProblemId()));
        final Result<byte[], ErrorResponse<String>> resp = minioService.getFile(problemFile.getStatementUuid(), ".pdf");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            return switch (resp) {
                case Result.Success(byte[] data) -> {
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "inline; filename=" + "Problem-" + problemSequence + ".pdf");
                    response.setHeader("Content-Length", String.valueOf(data.length));
                    outputStream.write(data);
                    outputStream.flush();

                    yield Result.success();
                }
                case Result.Failure(ErrorResponse error) -> Result.failure(error);
            };
        } catch (IOException e) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    public @NonNull Result<Void, ErrorResponse<String>> getContestProblemSample(
            @NonNull final Long contestId,
            @NonNull final Integer problemSequence,
            @NonNull final HttpServletResponse response
    ) {
        final QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
        final ContestProblem contestProblem = contestProblemMapper
                .selectOne(contestProblemQueryWrapper.eq("contest_id", contestId).eq("sequence", problemSequence));
        if (contestProblem == null) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such contest problem"));
        }

        if (!isAfterContestStarted(contestId)) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_FORBIDDEN, "Contest has not started yet"));
        }

        final QueryWrapper<ProblemFile> problemFileQueryWrapper = new QueryWrapper<>();
        final ProblemFile problemFile = problemFileMapper
                .selectOne(problemFileQueryWrapper.eq("problem_id", contestProblem.getProblemId()));
        final Result<byte[], ErrorResponse<String>> resp = minioService.getFile(problemFile.getSampleUuid(), ".zip");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            return switch (resp) {
                case Result.Success(byte[] data) -> {
                    response.setContentType("application/zip");
                    response.setHeader("Content-Disposition", "attachment; filename=" + "sample-" + problemSequence + ".zip");
                    response.setHeader("Content-Length", String.valueOf(data.length));
                    outputStream.write(data);
                    outputStream.flush();

                    yield Result.success();
                }
                case Result.Failure(ErrorResponse<String> error) -> Result.failure(error);
            };
        } catch (IOException e) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    public @NonNull Result<Void, ErrorResponse<String>> getContestProblemTestcase(
            @NonNull final Long contestId,
            @NonNull final Integer problemSequence,
            @NonNull final HttpServletResponse response
    ) {
        final QueryWrapper<ContestProblem> contestProblemQueryWrapper = new QueryWrapper<>();
        final ContestProblem contestProblem = contestProblemMapper
                .selectOne(contestProblemQueryWrapper.eq("contest_id", contestId).eq("sequence", problemSequence));
        if (contestProblem == null) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such contest problem"));
        }

        if (!isAfterContestStarted(contestId)) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_FORBIDDEN, "Contest has not started yet"));
        }

        final QueryWrapper<ProblemFile> problemFileQueryWrapper = new QueryWrapper<>();
        final ProblemFile problemFile = problemFileMapper
                .selectOne(problemFileQueryWrapper.eq("problem_id", contestProblem.getProblemId()));
        final Result<byte[], ErrorResponse<String>> resp = minioService.getFile(problemFile.getTestcaseUuid(), ".zip");
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            return switch (resp) {
                case Result.Success(byte[] data) -> {
                    response.setContentType("application/zip");
                    response.setHeader("Content-Disposition", "attachment; filename=" + "testcase-" + problemSequence + ".zip");
                    response.setHeader("Content-Length", String.valueOf(data.length));
                    outputStream.write(data);
                    outputStream.flush();

                    yield Result.success();
                }
                case Result.Failure(ErrorResponse error) -> Result.failure(error);
            };
        } catch (IOException e) {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage()));

        }
    }
}
