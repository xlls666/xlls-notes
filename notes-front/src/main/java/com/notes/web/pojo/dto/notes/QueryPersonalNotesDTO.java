package com.notes.web.pojo.dto.notes;

import com.notes.web.pojo.dto.base.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("查询个人笔记条件")
public class QueryPersonalNotesDTO extends PageDTO {
    @ApiModelProperty(value = "是否在回收站", example = "false",dataType = "Boolean")
    private Boolean recycle;
}
