package com.zhs.entity;

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
 * 简历对应的多个单位，多个职务问题，跟企业表无关系
 * </p>
 *
 * @author zhs
 * @since 2020-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ResumeCompany对象", description="简历对应的多个单位，多个职务问题，跟企业表无关系")
public class ResumeCompany implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "简历id")
    private Long resumeId;

    @ApiModelProperty(value = "单位")
    private String company;

    @ApiModelProperty(value = "职务")
    private String job;

    @ApiModelProperty(value = "是否是政治身份，0不是，1是")
    private Integer isPolitics;

    @JsonIgnore
    @ApiModelProperty(value = "删除标志，0默认，1删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
