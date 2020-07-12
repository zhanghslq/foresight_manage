package com.zhs.backmanageb.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zhs.backmanageb.entity.Contacts;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/7/11 20:00
 */
@Data
@ApiModel(value = "联系人展示",description = "联系人信息")
public class ContactsVO implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("联系人姓名")
    private String realName;





    @ApiModelProperty("单位名称")
    private String companyName;



    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "职务")
    private String job;

    @ApiModelProperty(value = "联系电话")
    private String concatPhone;

    @ApiModelProperty(value = "级别id")
    private Long levelId;

    @ApiModelProperty(value = "级别描述")
    private Integer levelName;

    @ApiModelProperty(value = "渠道")
    private String channel;

}
