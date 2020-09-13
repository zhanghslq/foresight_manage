package com.zhs.model.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/26 20:14
 */
@Data
@ApiModel("地区")
public class AreaBO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("地区名称")
    private String name;
    @ApiModelProperty("下属地区")
    private List<AreaBO> children;

}
