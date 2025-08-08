package com.notes.web.pojo.dto.pag;

import com.notes.domain.front.notes.PersonalNotes;

/**
 * 用于与Python RAG服务通信的DTO类
 */
public class StoreNotesDTO {
    private Long id;
    private Long notesUserId;
    private String tag;
    private String title;
    private String source;
    private String content;
    private String createTime;
    private String updateTime;

    // Constructors
    public StoreNotesDTO() {
    }

    public StoreNotesDTO(PersonalNotes notes) {
        this.id = notes.getId();
        this.notesUserId = notes.getNotesUserId();
        this.tag = notes.getTag();
        this.title = notes.getTitle();
        this.source = notes.getSource();
        this.content = notes.getContent();
        // 将LocalDateTime转换为字符串格式
        this.createTime = notes.getCreateTime() != null ? notes.getCreateTime().toString() : null;
        this.updateTime = notes.getUpdateTime() != null ? notes.getUpdateTime().toString() : null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNotesUserId() {
        return notesUserId;
    }

    public void setNotesUserId(Long notesUserId) {
        this.notesUserId = notesUserId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}