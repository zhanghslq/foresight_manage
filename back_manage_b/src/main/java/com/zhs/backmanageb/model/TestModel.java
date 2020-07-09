package com.zhs.backmanageb.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author: zhs
 * @date: 2020/6/1 10:55
 */
@ApiModel(value = "测试的数据类型")
@Data
public class TestModel {
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "密码")
    private String password;
}
