package com.zhs.backmanageb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private Integer id;

    @ApiModelProperty(value = "机构名称")
    private String name;

    @ApiModelProperty(value = "重要性")
    private Integer importance;

    @ApiModelProperty(value = "完善度")
    private Integer perfectValue;

    @ApiModelProperty(value = "行政级别")
    private Long levelId;

    @ApiModelProperty(value = "所属系统（应该算是上级id）")
    private Long parentId;

    @ApiModelProperty(value = "所属系统（比如全国人大，等等这种粒度的）")
    private Long systemId;

    @ApiModelProperty(value = "所属体系（军，政，法等）")
    private Long type;

    @ApiModelProperty(value = "是否是事业单位0否，1是")
    private String isGovernment;

    @ApiModelProperty(value = "所属类型")
    private Long companyTypeId;

    @ApiModelProperty(value = "协会学会0否1是")
    private Integer isAssociation;

    @ApiModelProperty(value = "是否脱钩1是，0否")
    private Integer isDecoupling;

    @ApiModelProperty(value = "网址")
    private String website;

    @ApiModelProperty(value = "地区id")
    private Long regionId;

    private String adddressDetail;

    @ApiModelProperty(value = "机构logo地址")
    private String logoUrl;

    @ApiModelProperty(value = "机构报告地址")
    private String reportUrl;

    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
