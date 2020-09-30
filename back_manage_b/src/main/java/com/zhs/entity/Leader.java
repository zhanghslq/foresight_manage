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
 * 领导人
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Leader对象", description="领导人")
public class Leader implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "组织机构")
    private Long organizationId;

    @ApiModelProperty(value = "职位")
    private String position;

    @ApiModelProperty(value = "级别id数组")
    private String levelIdArray;

    @ApiModelProperty(value = "级别")
    private Long levelId;

    @ApiModelProperty(value = "领导类型（军，政等，具体在枚举类维护）")
    private Integer type;

    @ApiModelProperty(value = "领导姓名")
    private String realName;

    @ApiModelProperty("模块id")
    private Long moduleId;

    @ApiModelProperty("排序")
    private Integer seq;

    @ApiModelProperty("简历id")
    private Long resumeId;

    @ApiModelProperty("是否有相同名字的简历")
    private Integer hasResume;

    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
