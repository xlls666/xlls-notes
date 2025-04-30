package com.notes.web.pojo.dto.notes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("新增个人笔记")
public class AddPersonalNotesDTO {
    @ApiModelProperty(value = "标签", example = "哲学/笛卡尔",dataType = "String")
    private String tag;
    @ApiModelProperty(value = "标题", example = "学习~学个屁",dataType = "String")
    private String title;
    @ApiModelProperty(value = "来源", example = "知乎",dataType = "String")
    private String source;
    @ApiModelProperty(value = "内容", example = "learn~ learn ass",dataType = "String")
    private String content;
}
