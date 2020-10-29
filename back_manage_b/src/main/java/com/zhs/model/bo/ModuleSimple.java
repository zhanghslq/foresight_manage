package com.zhs.model.bo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ModuleSimple {
    @ApiModelProperty("模块名")
    private String moduleName;
    @ApiModelProperty("数量")
    private Integer count;


}