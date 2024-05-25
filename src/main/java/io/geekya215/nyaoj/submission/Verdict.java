package io.geekya215.nyaoj.submission;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Verdict {
    WAITING(0, "waiting"),
    JUDGING(1, "judging"),
    ACCEPT(2, "accept"),
    WRONG_ANSWER(3, "wrong_answer"),
    TIME_LIMIT_EXCEED(4, "time_limit_exceed"),
    MEMORY_LIMIT_EXCEED(5, "memory_limit_exceed"),
    COMPILE_ERROR(6, "compile_error"),
    RUNTIME_ERROR(7, "runtime_error")
    ;
    @EnumValue
    @JsonValue
    private final int code;
    private final String value;

    Verdict(int code, String value) {
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
