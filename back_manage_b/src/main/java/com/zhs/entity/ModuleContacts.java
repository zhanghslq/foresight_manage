package com.zhs.entity;

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
 * 模块-联系人关系表
 * </p>
 *
 * @author zhs
 * @since 2020-08-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ModuleContacts对象", description="模块-联系人关系表")
public class ModuleContacts implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "模块id")
    private Long moduleId;

    @ApiModelProperty(value = "联系人id")
    private Long contactId;

    @ApiModelProperty(value = "排序")
    private Integer seq;

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
