package com.zhs.backmanageb.entity;

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
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="OrganizationType对象", description="")
public class OrganizationType implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "组织类别")
    private String name;

    @ApiModelProperty(value = "大的分类，比如军，政，等")
    private Integer type;

    @ApiModelProperty(value = "上级id，不选可传0，（最顶级的类别）")
    private Long parentId;

    @ApiModelProperty(value = "分类详情")
    private String detail;

    @ApiModelProperty(value = "是否需要关联地区，0否，1是")
    private Integer hasLocation;

    @ApiModelProperty(value = "排序")
    private Integer seq;


    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;


    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
