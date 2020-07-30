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

/**
 * <p>
 * 企业
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Company对象", description="企业")
public class Company implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "企业名称")
    private String name;

    @ApiModelProperty(value = "完善度")
    private Integer perfectValue;

    @ApiModelProperty(value = "企业行政级别")
    private Long companyLevelId;

    @ApiModelProperty(value = "上级关系类型")
    private Long relationshipTypeId;

    @ApiModelProperty(value = "是否上市")
    private Integer isMarket;

    private Long marketTypeId;

    @ApiModelProperty(value = "上市代码")
    private String markedCode;

    @ApiModelProperty(value = "机构网址")
    private String website;

    @ApiModelProperty("地区id")
    private Long areaId;

    @ApiModelProperty("地区名称")
    private String areaName;

    @ApiModelProperty("地址详情")
    private String addressDetail;



    @ApiModelProperty("企业logo")
    private String logoUrl;

    @ApiModelProperty(value = "企业报告")
    private String reportUrl;

    @ApiModelProperty("父类企业id")
    private Long parentId;

    @ApiModelProperty("企业类别id")
    private Long organizationTypeId;

    @ApiModelProperty("模块id")
    private Long moduleId;

    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
