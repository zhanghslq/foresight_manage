package com.zhs.backmanageb.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/8/4 18:33
 */
@Data
public class AdminOperationLogVO {
    @ApiModelProperty("操作时间")
    private Date createTime;
    @ApiModelProperty(value = "操作类型")
    private String operatorType;
    @ApiModelProperty(value = "接口描述")
    private String interfaceDesc;
}
