package com.zhs.backmanageb.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: zhs
 * @date: 2020/7/11 15:43
 */
@Data
@ApiModel("公共的类别")
@Accessors(chain = true)
public class CommonTypeVO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("名字")
    private String name;
}
