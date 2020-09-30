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
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;

/**
 * <p>
 * 组织机构
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="Organization对象", description="组织机构")
public class Organization implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "机构名称")
    private String name;

    @ApiModelProperty("其他名称")
    private String otherName;

    @ApiModelProperty(value = "重要性")
    private Integer importance;

    @ApiModelProperty(value = "完善度")
    private Integer perfectValue;

    @ApiModelProperty(value = "行政级别id数组")
    private String levelIdArray;

    @ApiModelProperty(value = "行政级别")
    private Long levelId;

    @ApiModelProperty(value = "上级组织id）")
    private Long parentId;

    @ApiModelProperty(value = "组织类别id，比如全国人大，等等这种粒度的")
    private Long organizationTypeId;

    @ApiModelProperty(value = "所属体系（军，政，法等）")
    private Integer type;

    @ApiModelProperty(value = "是否是事业单位0否，1是")
    private Integer isGovernment;

    @ApiModelProperty(value = "体系id数组")
    private String hierarchyIdArray;

    @ApiModelProperty(value = "体系id，系统配置进行维护")
    private Long hierarchyId;

    @ApiModelProperty(value = "系统id数组")
    private Long systemIdArray;

    @ApiModelProperty(value = "系统id，系统配置进行维护")
    private Long systemId;

    @ApiModelProperty(value = "冗余给二级联动用，可以用来存系统的上级id，根据这个来判断取哪个下拉框的值")
    private String systemTypeIdString;

    @ApiModelProperty(value = "所属类型数组")
    private String commonTypeIdArray;

    @ApiModelProperty(value = "所属类型(系统配置维护)")
    private Long commonTypeId;

    @ApiModelProperty(value = "协会学会0否1是")
    private Integer isAssociation;

    @ApiModelProperty(value = "是否脱钩1是，0否")
    private Integer isDecoupling;

    @ApiModelProperty(value = "网址")
    private String website;

    @ApiModelProperty("地区id，集合，json形式")
    private String areaIdArray;

    @ApiModelProperty(value = "地区id")
    private Long areaId;

    @ApiModelProperty(value = "地址名称")
    private String areaName;

    @ApiModelProperty(value = "地址详情")
    private String addressDetail;

    @ApiModelProperty(value = "机构logo地址")
    private String logoUrl;

    @ApiModelProperty(value = "机构报告地址")
    private String reportUrl;

    @ApiModelProperty("模块id")
    private Long moduleId;

    @ApiModelProperty("管理员id")
    private Long adminId;

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
