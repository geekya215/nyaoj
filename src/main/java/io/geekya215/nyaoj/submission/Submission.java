package io.geekya215.nyaoj.submission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Submission implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long contestProblemId;
    private Long userId;
    private String code;
    private Language language;
    private Verdict verdict;
    private Long timeUsage;
    private Long memoryUsage;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContestProblemId() {
        return contestProblemId;
    }

    public void setContestProblemId(Long contestProblemId) {
        this.contestProblemId = contestProblemId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public void setVerdict(Verdict verdict) {
        this.verdict = verdict;
    }

    public Long getTimeUsage() {
        return timeUsage;
    }

    public void setTimeUsage(Long timeUsage) {
        this.timeUsage = timeUsage;
    }

    public Long getMemoryUsage() {
        return memoryUsage;
    }

    public void setMemoryUsage(Long memoryUsage) {
        this.memoryUsage = memoryUsage;
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

        Submission that = (Submission) o;
        return Objects.equals(id, that.id)
                && Objects.equals(contestProblemId, that.contestProblemId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(code, that.code)
                && language == that.language
                && verdict == that.verdict
                && Objects.equals(timeUsage, that.timeUsage)
                && Objects.equals(memoryUsage, that.memoryUsage)
                && Objects.equals(createTime, that.createTime)
                && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(contestProblemId);
        result = 31 * result + Objects.hashCode(userId);
        result = 31 * result + Objects.hashCode(code);
        result = 31 * result + Objects.hashCode(language);
        result = 31 * result + Objects.hashCode(verdict);
        result = 31 * result + Objects.hashCode(timeUsage);
        result = 31 * result + Objects.hashCode(memoryUsage);
        result = 31 * result + Objects.hashCode(createTime);
        result = 31 * result + Objects.hashCode(updateTime);
        return result;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", contestProblemId=" + contestProblemId +
                ", userId=" + userId +
                ", code='" + code + '\'' +
                ", language=" + language +
                ", verdict=" + verdict +
                ", timeUsage=" + timeUsage +
                ", memoryUsage=" + memoryUsage +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
