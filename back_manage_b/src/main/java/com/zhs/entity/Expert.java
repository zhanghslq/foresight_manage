package com.zhs.entity;

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
 * 专家
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Expert对象", description="专家")
public class Expert implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "性别")
    private Integer sex;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "性别名称")
    private String sexName;

    @ExcelProperty(index = 1)
    @ApiModelProperty(value = "名字")
    private String realName;


    @ExcelProperty(index = 3)
    @ApiModelProperty(value = "单位")
    private String company;

    @ExcelProperty(index = 4)
    @ApiModelProperty(value = "部门")
    private String department;

    @ExcelProperty(index = 5)
    @ApiModelProperty(value = "职责")
    private String job;

    @ExcelProperty(index = 6)
    @ApiModelProperty(value = "联系电话")
    private String mobile;

    @ApiModelProperty(value = "专家类别id数组")
    private String classificationIdArray;

    @ApiModelProperty(value = "专家类别id")
    private Long classificationId;

    @ApiModelProperty(value = "专家类别名称")
    private String classificationName;

    @ApiModelProperty(value = "级别id数组")
    private String levelIdArray;

    @ApiModelProperty(value = "级别")
    private Long levelId;

    @ExcelProperty(index = 7)
    @ApiModelProperty(value = "级别名称")
    private String levelName;

    @ApiModelProperty("从事领域数组")
    private String workAreaIdArray;

    @ApiModelProperty(value = "从事领域id")
    private Long workAreaId;

    @ExcelProperty(index = 9)
    @ApiModelProperty(value = "从事领域")
    private String workArea;

    @ExcelProperty(index = 10)
    @ApiModelProperty(value = "细分领域")
    private String subdivisionArea;

    @ApiModelProperty("是否有微信")
    private Integer hasWechat;

    @ApiModelProperty("微信号")
    private String wechatNumber;

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

    @ExcelProperty(index = 8)
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
