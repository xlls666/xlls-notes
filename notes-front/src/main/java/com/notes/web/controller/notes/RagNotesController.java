package com.notes.web.controller.notes;

import com.notes.common.core.domain.R;
import com.notes.web.pojo.dto.pag.ChatNotesDTO;
import com.notes.web.service.notes.IRagNotesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "RAG个人笔记")
@RequestMapping("/rag")
@Validated
public class RagNotesController {

    @Autowired
    private IRagNotesService ragNotesService;

    @PostMapping("/chat")
    @ApiOperation("个人笔记chat")
    public R chat(@RequestBody ChatNotesDTO chatNotesDTO) {
        return R.ok(ragNotesService.chat(chatNotesDTO));
    }

    @PostMapping("/store/{noteId}")
    @ApiOperation("个人笔记store")
    public R store(@PathVariable Long noteId) {
        ragNotesService.storeNotes(noteId);
        return R.ok();
    }
}
