package com.zhs.model.vo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @since: 2020/11/4 16:39
 */
@Data
public class ResumeSexLevelVO {
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("级别")
    private String name;
    @ApiModelProperty("数量")
    private Integer value;
}
