package com.notes.web.service.notes;

import com.notes.common.core.domain.ApiResponse;
import com.notes.common.exception.notes.NotesDataException;
import com.notes.common.utils.RestTemplateUtil;
import com.notes.domain.front.notes.PersonalNotes;
import com.notes.web.pojo.dto.pag.ChatNotesDTO;
import com.notes.web.pojo.dto.pag.StoreNotesDTO;
import com.notes.frontframe.util.FrontSecurityUtils;
import com.notes.mapper.front.notes.PersonalNotesMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RagNotesServiceImpl implements IRagNotesService{

    private String baseUrl = "http://host.docker.internal:20002/rag";

    @Autowired
    private PersonalNotesMapper personalNotesMapper;

    @Override
    public void storeNotes(Long noteId) {
        PersonalNotes note = personalNotesMapper.selectById(noteId);
        if (note == null) {
            throw new NotesDataException(new Object[]{noteId});
        }
        
        // 转换为DTO对象
        StoreNotesDTO noteDTO = new StoreNotesDTO(note);
        
        // 调用外部服务API
        RestTemplateUtil.postForObject(baseUrl + "/store", noteDTO, ApiResponse.class);
    }

    @Override
    public Object chat(ChatNotesDTO chatNotesDTO) {
        Long userId = FrontSecurityUtils.getUserId();
        chatNotesDTO.setUserId(String.valueOf(userId)); // 确保 userId 被正确设置

        ResponseEntity<ApiResponse> response = RestTemplateUtil.postForObject(baseUrl + "/chat", chatNotesDTO, ApiResponse.class);
        System.out.println(response.getStatusCodeValue());
        System.out.println(response.getBody());

        return response.getBody();
    }


}