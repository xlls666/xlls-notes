package com.notes.domain.front.notes;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 个人笔记表
 * </p>
 *
 * @author ldj
 * @since 2025-04-30
 */
@Getter
@Setter
@TableName("personal_notes")
public class PersonalNotes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("notes_user_id")
    private Long notesUserId;

    /**
     * 笔记标签
     */
    @TableField("tag")
    private String tag;

    /**
     * 笔记标题
     */
    @TableField("title")
    private String title;

    /**
     * 笔记出处
     */
    @TableField("source")
    private String source;

    /**
     * 笔记内容
     */
    @TableField("content")
    private String content;

    /**
     * 软删除标志 (1: 已删除, 0: 未删除)
     */
    @TableField("del")
    @TableLogic
    private Boolean del;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 添加到回收站
     */
    @TableField("recycle")
    private Boolean recycle;

    /**
     * 回收删除时间
     */
    @TableField("recycle_time")
    private LocalDateTime recycleTime;

    /**
     * 可存储到es的时间，时间=时间戳开始时间 =》无需固化
     */
    @TableField("store_es_time")
    private LocalDateTime storeEsTime;

    public PersonalNotes() {
    }

    public void addInit() {
        this.del = false;
        this.recycle = false;
        LocalDateTime now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }
}
