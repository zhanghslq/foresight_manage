package com.zhs.backmanageb.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 联系人
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Contacts对象", description="联系人")
public class Contacts implements Serializable {

    private static final long serialVersionUID=1L;

    @ExcelProperty(index = 0)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    @ExcelProperty(index = 1)
    @ApiModelProperty("联系人姓名")
    private String realName;

    @ExcelProperty(index = 3)
    @ApiModelProperty(value = "单位名字")
    private String companyName;


    @ExcelProperty(index = 4)
    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ExcelProperty(index = 5)
    @ApiModelProperty(value = "职务")
    private String job;

    @ExcelProperty(index = 6)
    @ApiModelProperty(value = "联系电话")
    private String concatPhone;

    @ApiModelProperty(value = "级别id")
    private Long levelId;

    @ExcelProperty(index = 7)
    @ApiModelProperty(value = "级别名称")
    private String levelName;

    @ApiModelProperty(value = "性别id")
    private Integer sex;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "性别名称")
    private String sexName;

    @ExcelProperty(index = 9)
    @ApiModelProperty(value = "渠道")
    private String channel;

    @ApiModelProperty("排序")
    private Integer seq;

    @JsonIgnore
    private Integer pushed;



    @ApiModelProperty("管理员id")
    private Long adminId;

    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelProperty(index = 8)
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

}
