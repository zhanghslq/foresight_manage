package com.zhs.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/10/29 14:07
 */
@Data
public class ModuleFrontBO {
    @ApiModelProperty("模块名称")
    private String moduleName;
    @ApiModelProperty("模块下的机构列表")
    private List<OrganizationSimple> organizationSimpleList;
}
