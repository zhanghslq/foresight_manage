package com.zhs.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/2 15:42
 */
@Data
@ApiModel("页面")
public class PageBO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("页面名称")
    private String name;
    @ApiModelProperty("页面描述")
    private String description;
    private List<PageBO> children;
}
