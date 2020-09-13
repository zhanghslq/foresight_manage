package com.zhs.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/8/4 18:33
 */
@Data
public class AdminOperationLogVO {
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty("操作时间")
    private Date createTime;
    @ApiModelProperty(value = "操作类型")
    private String operatorType;
    @ApiModelProperty(value = "接口描述")
    private String interfaceDesc;
}
