package com.zhs.backmanageb.model.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/23 14:13
 */
@Data
public class OrganizationTypeBO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "组织类别")
    private String name;

    @ApiModelProperty(value = "大的分类，比如军，政，等")
    private Integer type;

    private List<OrganizationTypeBO> children;

    private Long parentId;

    @ApiModelProperty(value = "分类详情")
    private String detail;


}
