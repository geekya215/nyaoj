package io.geekya215.nyaoj.contest;

import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.contest.dto.AddProblemRequest;
import io.geekya215.nyaoj.contest.dto.CreateContestRequest;
import io.geekya215.nyaoj.contest.dto.SingleContestResponse;
import io.geekya215.nyaoj.registration.RegistrationService;
import io.geekya215.nyaoj.submission.CreateSubmissionRequest;
import io.geekya215.nyaoj.submission.SubmissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contests")
public class ContestController {
    private final ContestService contestService;
    private final RegistrationService registrationService;
    private final SubmissionService submissionService;

    public ContestController(ContestService contestService,
                             RegistrationService registrationService,
                             SubmissionService submissionService) {
        this.contestService = contestService;
        this.registrationService = registrationService;
        this.submissionService = submissionService;
    }

    @PostMapping
    public @NonNull ResponseEntity<?> createContest(@RequestBody @Valid CreateContestRequest createContestRequest) {
        return switch (contestService.createContest(createContestRequest)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @GetMapping("/{id}")
    public @NonNull ResponseEntity<?> getContestById(@PathVariable @Positive Long id) {
        return switch (contestService.getContestById(id)) {
            case Result.Success(SingleContestResponse singleContestResponse) ->
                    ResponseEntity.ok(singleContestResponse);
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/{id}/problems")
    public @NonNull ResponseEntity<?> addContestProblem(
            @PathVariable @Positive Long id,
            @RequestBody @Valid AddProblemRequest addProblemRequest
    ) {
        return switch (contestService.addContestProblem(id, addProblemRequest)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @GetMapping("/{cid}/problems/{pseq}/statement")
    public @NonNull ResponseEntity<?> getContestProblemStatement(
            @PathVariable @Positive Long cid,
            @PathVariable @Positive Integer pseq,
            HttpServletResponse response
    ) {
        return switch (contestService.getContestProblemStatement(cid, pseq, response)) {
            case Result.Success _ -> ResponseEntity.ok().build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @GetMapping("/{cid}/problems/{pseq}/sample")
    public @NonNull ResponseEntity<?> getContestProblemSample(
            @PathVariable @Positive Long cid,
            @PathVariable @Positive Integer pseq,
            HttpServletResponse response
    ) {
        return switch (contestService.getContestProblemSample(cid, pseq, response)) {
            case Result.Success _ -> ResponseEntity.ok().build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @GetMapping("/{cid}/problems/{pseq}/testcase")
    public @NonNull ResponseEntity<?> getContestProblemTestcase(
            @PathVariable @Positive Long cid,
            @PathVariable @Positive Integer pseq,
            HttpServletResponse response
    ) {
        return switch (contestService.getContestProblemTestcase(cid, pseq, response)) {
            case Result.Success _ -> ResponseEntity.ok().build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @GetMapping
    public @NonNull ResponseEntity<?> getContestList(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Range(min = 10, max = 20) Integer size
    ) {
        return switch (contestService.getContestList(page, size)) {
            case Result.Success(List<SingleContestResponse> contests) -> ResponseEntity.ok(contests);
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/{id}/registrations")
    public @NonNull ResponseEntity<?> createContestRegistration(
            @PathVariable @Positive Long id,
            HttpServletRequest request
    ) {
        final Long userId = ((Long) request.getAttribute("userId"));
        return switch (registrationService.createRegistration(userId, id)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }

    @PostMapping("/{id}/problems/{seq}/submissions")
    public @NonNull ResponseEntity<?> createContestProblemSubmission(
            @PathVariable @Positive Long id,
            @PathVariable @Positive Integer seq,
            @RequestBody @Valid CreateSubmissionRequest createSubmissionRequest,
            HttpServletRequest request
    ) {
        final Long userId = (Long) request.getAttribute("userId");
        return switch (submissionService.createSubmission(userId, id, seq, createSubmissionRequest)) {
            case Result.Success _ -> ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
            case Result.Failure(ErrorResponse<String> error) -> ResponseEntity.status(error.statusCode()).body(error);
        };
    }
}
