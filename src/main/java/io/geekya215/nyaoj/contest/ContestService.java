package io.geekya215.nyaoj.contest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.geekya215.nyaoj.common.ErrorResponse;
import io.geekya215.nyaoj.common.Result;
import io.geekya215.nyaoj.contest.dto.SingleContestResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContestService {
    private final ContestMapper contestMapper;

    public ContestService(ContestMapper contestMapper) {
        this.contestMapper = contestMapper;
    }

    public @NonNull Result<SingleContestResponse, ErrorResponse<String>> getContestById(@NonNull Long contestId) {
        final Contest contest = contestMapper.selectById(contestId);
        if (contest != null) {
            return Result.success(new SingleContestResponse(
                    contest.getTitle(), contest.getDescription(), contest.getStartTime(), contest.getEndTime()));
        } else {
            return Result.failure(ErrorResponse.of(HttpServletResponse.SC_NOT_FOUND, "not such contest"));
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
}
