package com.notes.web.pojo.vo.notes;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IndexNotesListVO {
    private Long id;
    private String tag;
    private String title;
    private String source;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
