package com.notes.web.pojo.dto.notes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel("修改个人笔记")
public class EditPersonalNotesDTO extends AddPersonalNotesDTO {
    @ApiModelProperty(value = "笔记id", example = "1",dataType = "Long")
    @NotNull
    private Long id;

    @ApiModelProperty(value = "用户id", example = "1",dataType = "Long")
    @NotNull
    private Long notesUserId;
}
