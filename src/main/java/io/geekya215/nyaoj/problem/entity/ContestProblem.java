package io.geekya215.nyaoj.problem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class ContestProblem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long contestId;
    private Long problemId;
    private Integer sequence;
    private Integer color; // hex
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
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

        ContestProblem that = (ContestProblem) o;
        return Objects.equals(id, that.id)
                && Objects.equals(contestId, that.contestId)
                && Objects.equals(problemId, that.problemId)
                && Objects.equals(sequence, that.sequence)
                && Objects.equals(color, that.color)
                && Objects.equals(createTime, that.createTime)
                && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(contestId);
        result = 31 * result + Objects.hashCode(problemId);
        result = 31 * result + Objects.hashCode(sequence);
        result = 31 * result + Objects.hashCode(color);
        result = 31 * result + Objects.hashCode(createTime);
        result = 31 * result + Objects.hashCode(updateTime);
        return result;
    }

    @Override
    public String toString() {
        return "ContestProblem{" +
                "id=" + id +
                ", contestId=" + contestId +
                ", problemId=" + problemId +
                ", sequence=" + sequence +
                ", color=" + color +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
