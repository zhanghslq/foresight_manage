package com.zhs.backmanageb.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/8/11 12:24
 */
@Data
public class InputStatisticsVO {
    @ApiModelProperty("类别id")
    private Long id;
    @ApiModelProperty("类别名称")
    private String name;
    @ApiModelProperty(value = "数量")
    private Integer count;
}
