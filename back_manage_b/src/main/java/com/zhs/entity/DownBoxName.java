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
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户创建的下拉框名字
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Accessors(chain = true)
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="DownBoxName对象", description="用户创建的下拉框名字")
public class DownBoxName implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    @ApiModelProperty(value = "下拉框类型的id")
    private Integer downBoxTypeId;

    @ApiModelProperty(value = "上级id")
    private Integer parentId;

    @JsonIgnore
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
