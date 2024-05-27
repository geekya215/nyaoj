package io.geekya215.nyaoj.problem;

import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.problem.dto.CreateProblemRequest;
import io.geekya215.nyaoj.util.FileChecker;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/problems")
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping
    public @NonNull ResponseEntity<?> createProblem(@RequestBody @Valid CreateProblemRequest createProblemRequest) {
        return switch (problemService.createProblem(createProblemRequest)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping(value = "/{id}/statements")
    public @NonNull ResponseEntity<?> addProblemStatement(
            @PathVariable @Positive Long id,
            @RequestParam MultipartFile statement
    ) {
        if (!FileChecker.isPdf(statement)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE).body("statement file must be pdf type");
        }

        return switch (problemService.addProblemStatement(id, statement, "application/pdf")) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/{id}/samples")
    public @NonNull ResponseEntity<?> addProblemSample(
            @PathVariable @Positive Long id,
            @RequestParam MultipartFile sample
    ) {
        if (!FileChecker.isZip(sample)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE).body("sample file must be zip type");
        }

        if (!FileChecker.validateZip(sample)) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body("sample in zip file is not valid pattern");
        }

        return switch (problemService.addProblemSample(id, sample, "application/zip")) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/{id}/testcases")
    public @NonNull ResponseEntity<?> addProblemTestcase(
            @PathVariable @Positive Long id,
            @RequestParam MultipartFile testcase
    ) {
        if (!FileChecker.isZip(testcase)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE).body("testcase file must be zip type");
        }

        if (!FileChecker.validateZip(testcase)) {
            return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body("testcase in zip file is not valid pattern");
        }

        return switch (problemService.addProblemTestcase(id, testcase, "application/zip")) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }
}
