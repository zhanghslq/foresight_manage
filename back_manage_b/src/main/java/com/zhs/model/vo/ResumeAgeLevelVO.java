package com.zhs.model.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;

/**
 * @author: zhs
 * @since: 2020/11/4 16:37
 */
@Data
public class ResumeAgeLevelVO {

    @ApiModelProperty("年龄")
    private String age;
    @ApiModelProperty("级别")
    private String level;
    @ApiModelProperty("数量")
    private Integer value;
}
