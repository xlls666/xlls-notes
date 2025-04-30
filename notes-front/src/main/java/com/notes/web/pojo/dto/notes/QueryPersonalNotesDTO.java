package com.notes.web.pojo.dto.notes;

import com.notes.web.pojo.dto.base.PageDTO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel("查询个人笔记条件")
public class QueryPersonalNotesDTO extends PageDTO {
}
