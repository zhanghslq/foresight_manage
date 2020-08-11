package com.zhs.backmanageb.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/8/11 12:24
 */
@Data
public class ExpertInputStatisticsVO {
    @ApiModelProperty("专家类别id")
    private Long id;
    @ApiModelProperty("专家类别名称")
    private String name;
    @ApiModelProperty(value = "录入数量")
    private Integer count;
}
