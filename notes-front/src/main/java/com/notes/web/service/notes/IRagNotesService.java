package com.notes.web.service.notes;

import com.notes.domain.front.notes.PersonalNotes;
import com.notes.web.pojo.dto.pag.ChatNotesDTO;

/**
 * RAG笔记服务接口
 */
public interface IRagNotesService {

    /**
     * 存储笔记到RAG系统
     * @param noteId 笔记ID
     */
    void storeNotes(Long noteId);

    Object chat(ChatNotesDTO chatNotesDTO);
}