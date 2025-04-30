package com.notes.web.pojo.dto.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@ApiModel("分页参数")
@Data
public class PageDTO {
    @ApiModelProperty(value = "页码", example = "1",dataType = "Integer")
    private Integer current;
    @ApiModelProperty(value = "每页数量", example = "10",dataType = "Integer")
    private Integer size;
}
