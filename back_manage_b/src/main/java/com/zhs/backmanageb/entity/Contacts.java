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

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("组织体系，军政法等")
    private Integer type;

    @ApiModelProperty("联系人姓名")
    private String realName;

    @ApiModelProperty("是否是企业的联系人，默认0，否")
    private Integer isCompany;

    @ApiModelProperty(value = "哪个机构的联系人")
    private Long organizationId;

    @ApiModelProperty(value = "单位id")
    private Long companyId;

    @ApiModelProperty(value = "单位名字")
    private String companyName;

    @ApiModelProperty(value = "部门id")
    private Long departmentId;

    @ApiModelProperty(value = "部门名称")
    private String departmentName;

    @ApiModelProperty(value = "职务")
    private String job;

    @ApiModelProperty(value = "联系电话")
    private String concatPhone;

    @ApiModelProperty(value = "级别id")
    private String levelId;

    @ApiModelProperty(value = "渠道")
    private String channel;

    @JsonIgnore
    private Integer pushed;

    @ApiModelProperty("模块id")
    private Long moduleId;

    @ApiModelProperty("管理员id")
    private Long adminId;

    @JsonIgnore
    @ApiModelProperty(value = "删除标志")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
