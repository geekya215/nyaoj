package io.geekya215.nyaoj.contest;

import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.contest.dto.AddProblemRequest;
import io.geekya215.nyaoj.contest.dto.CreateContestRequest;
import io.geekya215.nyaoj.contest.dto.SingleContestResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contests")
public class ContestController {
    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
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
            case Result.Success _ -> ResponseEntity.noContent().build();
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
}
