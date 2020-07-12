package com.zhs.backmanageb.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

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
@ApiModel(value="OrganizationTypeInsertVO对象,用于插入", description="")
public class OrganizationTypeInsertVO implements Serializable {
    private static final long serialVersionUID=1L;
    @ApiModelProperty(value = "组织类别")
    private String name;

    @ApiModelProperty(value = "大的分类，比如军，政，等")
    private Integer type;

    @ApiModelProperty(value = "上级id，不选可传0，（最顶级的类别）")
    private Long parentId;

    @ApiModelProperty(value = "分类详情")
    private String detail;


}
