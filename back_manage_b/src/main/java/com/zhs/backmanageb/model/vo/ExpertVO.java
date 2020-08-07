package com.zhs.backmanageb.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/8/7 18:09
 */
@Data
public class ExpertVO {
    @ApiModelProperty(value = "编号")
    private Long id;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ApiModelProperty(value = "名字")
    private String realName;

    @ApiModelProperty(value = "单位")
    private String company;

    @ApiModelProperty(value = "部门")
    private String department;

    @ApiModelProperty(value = "职责")
    private String job;

    @ApiModelProperty(value = "联系电话")
    private String mobile;

    @ApiModelProperty(value = "级别")
    private Long levelId;

    @ApiModelProperty(value = "级别名称")
    private String levelName;

    @ApiModelProperty(value = "从事领域")
    private String workArea;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @JsonIgnore
    private Integer pushed;

    @JsonIgnore
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
