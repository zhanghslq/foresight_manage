package com.zhs.model.bo;

import com.zhs.entity.Leader;
import com.zhs.entity.Organization;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/25 17:57
 */
@Data
@ApiModel("组织模块")
public class OrganizationModuleBO {
    /**
     * 模块类型
     */
    @ApiModelProperty("组织模块的类型，0领导人模块，1下属组织模块，2联系人模块，根据type，取对应属性的值")
    private Integer type;

    @ApiModelProperty("模块名称")
    private String moduleName;

    @ApiModelProperty("模块id")
    private Long moduleId;
    /**
     * 下属组织
     */
    private List<Organization> organizationChildren;

    /**
     * 领导人
     */
    private List<Leader> leaders;

    /**
     * 联系人
     */
    private List<ContactsBO> contacts;

    /**
     * 组织机构本身
     */
    private Organization organization;
}
