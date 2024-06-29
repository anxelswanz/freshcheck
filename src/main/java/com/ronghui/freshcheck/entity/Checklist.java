package com.ronghui.freshcheck.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author ansel
 * @since 2024-06-14
 */
public class Checklist implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;

    private Integer count;

    private String checkId;

    private String content;

    private String userId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getCheckId() {
        return checkId;
    }

    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Checklist{" +
            "title=" + title +
            ", count=" + count +
            ", checkId=" + checkId +
            ", content=" + content +
            ", userId=" + userId +
        "}";
    }
}
