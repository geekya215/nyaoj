package io.geekya215.nyaoj.submission;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {
    C(1, "c"),
    CPP(2, "cpp"),
    JAVA(3, "java"),
    GO(4, "go"),
    PYTHON(5, "python"),
    JAVASCRIPT(6, "javascript")
    ;

    @EnumValue
    @JsonValue
    private final int code;
    private final String value;

    Language(int code, String value) {
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
