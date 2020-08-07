package com.zhs.backmanageb.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 组织模块
 * </p>
 *
 * @author zhs
 * @since 2020-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="OrganizationModule对象", description="组织模块")
public class OrganizationModule implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "模块名称")
    private String name;

    @ApiModelProperty(value = "组织id")
    private Long organizationId;

    @ApiModelProperty(value = "组织模块类型，0领导人，1下属机构，2联系人,3下属企业")
    private Integer type;

    @ApiModelProperty("是否是企业，默认0否,1是")
    private Integer isCompany;

    @ApiModelProperty("排序")
    private Integer seq;

    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
