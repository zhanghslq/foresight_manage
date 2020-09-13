package com.zhs.model.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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

    @ApiModelProperty(value = "是否关联地区,0否，1是")
    private Integer hasLocation;

    @ApiModelProperty(value = "地区id")
    private Long areaId;

    @ApiModelProperty(value = "地区名称")
    private String areaName;

    @ApiModelProperty("排序")
    private Integer seq;

    private List<OrganizationTypeBO> children;

    private Long parentId;

    @ApiModelProperty(value = "分类详情")
    private String detail;


}
