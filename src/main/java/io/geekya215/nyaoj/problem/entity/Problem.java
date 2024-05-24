package io.geekya215.nyaoj.problem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public final class Problem implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Integer timeLimit; // ms
    private Integer memoryLimit; // byte
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
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

        Problem problem = (Problem) o;
        return Objects.equals(id, problem.id)
                && Objects.equals(title, problem.title)
                && Objects.equals(timeLimit, problem.timeLimit)
                && Objects.equals(memoryLimit, problem.memoryLimit)
                && Objects.equals(createTime, problem.createTime)
                && Objects.equals(updateTime, problem.updateTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(id);
        result = 31 * result + Objects.hashCode(title);
        result = 31 * result + Objects.hashCode(timeLimit);
        result = 31 * result + Objects.hashCode(memoryLimit);
        result = 31 * result + Objects.hashCode(createTime);
        result = 31 * result + Objects.hashCode(updateTime);
        return result;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", timeLimit=" + timeLimit +
                ", memoryLimit=" + memoryLimit +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
