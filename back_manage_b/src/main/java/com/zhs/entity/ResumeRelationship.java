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
 * 简历关系表
 * </p>
 *
 * @author zhs
 * @since 2020-08-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ResumeRelationship对象", description="简历关系表")
public class ResumeRelationship implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "原始简历id")
    private Long sourceResumeId;

    @ApiModelProperty(value = "关联简历id")
    private Long targetResumeId;

    @ApiModelProperty(value = "关系id")
    private Long relationshipId;

    @ApiModelProperty(value = "关系名称")
    private String relationship;

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
