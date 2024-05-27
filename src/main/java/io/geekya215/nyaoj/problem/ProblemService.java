package io.geekya215.nyaoj.problem;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.problem.dto.CreateProblemRequest;
import io.geekya215.nyaoj.problem.entity.Problem;
import io.geekya215.nyaoj.problem.entity.ProblemFile;
import io.geekya215.nyaoj.problem.mapper.ProblemFileMapper;
import io.geekya215.nyaoj.problem.mapper.ProblemMapper;
import io.geekya215.nyaoj.storage.MinioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class ProblemService {
    private final ProblemMapper problemMapper;
    private final ProblemFileMapper problemFileMapper;
    private final MinioService minioService;

    public ProblemService(ProblemMapper problemMapper, ProblemFileMapper problemFileMapper, MinioService minioService) {
        this.problemMapper = problemMapper;
        this.problemFileMapper = problemFileMapper;
        this.minioService = minioService;
    }

    private boolean checkProblemExist(@NonNull final Long problemId) {
        final QueryWrapper<Problem> wrapper = new QueryWrapper<>();
        return problemMapper.exists(wrapper.eq("id", problemId));
    }

    public @NonNull Result<Void, ErrorResponse<String>> createProblem(@NonNull final CreateProblemRequest createProblemRequest) {
        final Problem problem = new Problem();

        problem.setTitle(createProblemRequest.title());
        problem.setTimeLimit(createProblemRequest.timeLimit());
        problem.setMemoryLimit(createProblemRequest.memoryLimit());

        problemMapper.insert(problem);

        return Result.success();
    }

    public @NonNull Result<Void, ErrorResponse<String>> addProblemStatement(
            @NonNull final Long problemId,
            @NonNull final MultipartFile statementFile,
            @NonNull final String contentType
    ) {
        if (checkProblemExist(problemId)) {
            final UUID uuid = UUID.randomUUID();
            final QueryWrapper<ProblemFile> problemFileQueryWrapper = new QueryWrapper<>();

            if (problemFileMapper.exists(problemFileQueryWrapper.eq("problem_id", problemId))) {
                final UpdateWrapper<ProblemFile> problemFileUpdateWrapper = new UpdateWrapper<>();
                problemFileMapper.update(problemFileUpdateWrapper
                        .eq("problem_id", problemId)
                        .set("statement_uuid", uuid.toString()));
            } else {
                final ProblemFile problemFile = new ProblemFile();
                problemFile.setStatementUuid(uuid.toString());
                problemFile.setProblemId(problemId);
                problemFileMapper.insert(problemFile);
            }

            return minioService.putFile(uuid, statementFile, ".pdf", contentType);
        } else {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such problem"));
        }
    }

    public @NonNull Result<Void, ErrorResponse<String>> addProblemSample(
            @NonNull final Long problemId,
            @NonNull final MultipartFile sampleFile,
            @NonNull final String contentType
    ) {
        if (checkProblemExist(problemId)) {
            final UUID uuid = UUID.randomUUID();
            final QueryWrapper<ProblemFile> problemFileQueryWrapper = new QueryWrapper<>();

            if (problemFileMapper.exists(problemFileQueryWrapper.eq("problem_id", problemId))) {
                final UpdateWrapper<ProblemFile> problemFileUpdateWrapper = new UpdateWrapper<>();
                problemFileMapper.update(problemFileUpdateWrapper
                        .eq("problem_id", problemId)
                        .set("sample_uuid", uuid.toString()));
            } else {
                final ProblemFile problemFile = new ProblemFile();
                problemFile.setSampleUuid(uuid.toString());
                problemFile.setProblemId(problemId);
                problemFileMapper.insert(problemFile);
            }

            return minioService.putFile(uuid, sampleFile, ".zip", contentType);
        } else {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such problem"));
        }
    }

    public @NonNull Result<Void, ErrorResponse<String>> addProblemTestcase(
            @NonNull final Long problemId,
            @NonNull final MultipartFile testcaseFile,
            @NonNull final String contentType
    ) {
        if (checkProblemExist(problemId)) {
            final UUID uuid = UUID.randomUUID();
            final QueryWrapper<ProblemFile> problemFileQueryWrapper = new QueryWrapper<>();

            if (problemFileMapper.exists(problemFileQueryWrapper.eq("problem_id", problemId))) {
                final UpdateWrapper<ProblemFile> problemFileUpdateWrapper = new UpdateWrapper<>();
                problemFileMapper.update(problemFileUpdateWrapper
                        .eq("problem_id", problemId)
                        .set("testcase_uuid", uuid.toString()));
            } else {
                final ProblemFile problemFile = new ProblemFile();
                problemFile.setTestcaseUuid(uuid.toString());
                problemFile.setProblemId(problemId);
                problemFileMapper.insert(problemFile);
            }

            return minioService.putFile(uuid, testcaseFile, ".zip", contentType);
        } else {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "no such problem"));
        }
    }
}
