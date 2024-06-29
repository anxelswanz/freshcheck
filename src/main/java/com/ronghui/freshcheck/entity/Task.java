package com.ronghui.freshcheck.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ansel
 * @since 2024-06-15
 */
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    private String createTime;

    /**
     * 0 no 1 yes
     */
    private Integer ifFinished;

    private String userId;

    private String checklistId;

    @TableId
    private String taskId;

    private String unfinishedTask;

    @TableField(exist = false)
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUnfinishedTask() {
        return unfinishedTask;
    }

    public void setUnfinishedTask(String unfinishedTask) {
        this.unfinishedTask = unfinishedTask;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public Integer getIfFinished() {
        return ifFinished;
    }

    public void setIfFinished(Integer ifFinished) {
        this.ifFinished = ifFinished;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getChecklistId() {
        return checklistId;
    }

    public void setChecklistId(String checklistId) {
        this.checklistId = checklistId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "createTime='" + createTime + '\'' +
                ", ifFinished=" + ifFinished +
                ", userId='" + userId + '\'' +
                ", checklistId='" + checklistId + '\'' +
                ", taskId='" + taskId + '\'' +
                ", unfinishedTask='" + unfinishedTask + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
