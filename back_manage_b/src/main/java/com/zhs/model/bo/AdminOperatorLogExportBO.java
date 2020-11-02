package com.zhs.model.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: zhs
 * @since: 2020/11/2 10:35
 */
@Data
public class AdminOperatorLogExportBO {

    @ExcelProperty(index = 0)
    @ApiModelProperty(value = "操作类型")
    private String operatorType;

    @ExcelProperty(index = 1)
    @ApiModelProperty(value = "管理员id")
    private Long adminId;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "管理员名称")
    private String adminName;

    @ExcelProperty(index = 3)
    @ApiModelProperty(value = "接口描述")
    private String interfaceDesc;

    @ExcelProperty(index = 4)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "操作时间")
    private Date createTime;
}
