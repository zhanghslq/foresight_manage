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
 * 
 * </p>
 *
 * @author zhs
 * @since 2020-08-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="AdminOperationLog对象", description="")
public class AdminOperationLog implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作类型")
    private String operatorType;

    @ApiModelProperty("用户ip")
    private String ip;

    @ApiModelProperty(value = "管理员id")
    private Long adminId;

    @ApiModelProperty(value = "请求类名")
    private String declaringName;

    @ApiModelProperty(value = "接口描述")
    private String interfaceDesc;

    @ApiModelProperty(value = "接口全路径名")
    private String interfaceAllName;

    @ApiModelProperty(value = "接口名")
    private String interfaceName;

    @ApiModelProperty(value = "方法名")
    private String methodName;

    @ApiModelProperty(value = "params参数")
    private String params;

    @ApiModelProperty(value = "body类型参数")
    private String body;

    @ApiModelProperty(value = "返回值,超过6万截取")
    private String returnString;

    @ApiModelProperty(value = "接口耗时")
    private Long useTime;

    @JsonIgnore
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
