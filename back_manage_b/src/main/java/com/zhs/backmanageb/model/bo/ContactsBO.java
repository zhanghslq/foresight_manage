package com.zhs.backmanageb.model.bo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/8/18 18:21
 */
@Data
public class ContactsBO {
    private Long id;

    @ApiModelProperty("联系人姓名")
    private String realName;

    @ApiModelProperty(value = "单位名字")
    private String companyName;


    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "职务")
    private String job;

    @ApiModelProperty(value = "联系电话")
    private String concatPhone;

    @ApiModelProperty(value = "默认电话")
    private String defaultPhone;

    @ApiModelProperty(value = "级别id")
    private Long levelId;

    @ApiModelProperty(value = "级别名称")
    private String levelName;

    @ApiModelProperty(value = "性别id")
    private Integer sex;

    @ApiModelProperty(value = "性别名称")
    private String sexName;

    @ApiModelProperty(value = "渠道")
    private String channel;


    @ApiModelProperty("管理员id")
    private Long adminId;

    @ApiModelProperty(value = "简历id")
    private Long resumeId;

    @ApiModelProperty(value = "是否与机构关联，默认0未关联，1关联")
    private Integer associatedWithOrganization;



    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    private Integer seq;
}
