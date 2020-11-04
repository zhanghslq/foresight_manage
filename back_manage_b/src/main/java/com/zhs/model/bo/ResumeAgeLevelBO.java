package com.zhs.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @since: 2020/11/4 16:37
 */
@Data
public class ResumeAgeLevelBO {
    @ApiModelProperty("出生年份")
    private Integer birthYear;
    @ApiModelProperty("年龄")
    private Integer age;
    @ApiModelProperty("级别")
    private Long levelId;
    @ApiModelProperty("数量")
    private Integer value;
}
