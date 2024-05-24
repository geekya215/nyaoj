package io.geekya215.nyaoj.problem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class ProblemFile implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long problemId;
    private String statementUuid;
    private String sampleUuid;
    private String testcaseUuid;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getStatementUuid() {
        return statementUuid;
    }

    public void setStatementUuid(String statementUuid) {
        this.statementUuid = statementUuid;
    }

    public String getSampleUuid() {
        return sampleUuid;
    }

    public void setSampleUuid(String sampleUuid) {
        this.sampleUuid = sampleUuid;
    }

    public String getTestcaseUuid() {
        return testcaseUuid;
    }

    public void setTestcaseUuid(String testcaseUuid) {
        this.testcaseUuid = testcaseUuid;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProblemFile that = (ProblemFile) o;
        return Objects.equals(id, that.id)
                && Objects.equals(problemId, that.problemId)
                && Objects.equals(statementUuid, that.statementUuid)
                && Objects.equals(sampleUuid, that.sampleUuid)
                && Objects.equals(testcaseUuid, that.testcaseUuid)
                && Objects.equals(createTime, that.createTime)
                && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(problemId);
        result = 31 * result + Objects.hashCode(statementUuid);
        result = 31 * result + Objects.hashCode(sampleUuid);
        result = 31 * result + Objects.hashCode(testcaseUuid);
        result = 31 * result + Objects.hashCode(createTime);
        result = 31 * result + Objects.hashCode(updateTime);
        return result;
    }

    @Override
    public String toString() {
        return "ProblemFile{" +
                "id=" + id +
                ", problemId=" + problemId +
                ", statementUuid='" + statementUuid + '\'' +
                ", sampleUuid='" + sampleUuid + '\'' +
                ", testcaseUuid='" + testcaseUuid + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
