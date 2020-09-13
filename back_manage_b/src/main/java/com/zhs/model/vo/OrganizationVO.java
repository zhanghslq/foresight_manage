package com.zhs.model.vo;

import com.zhs.entity.Organization;
import com.zhs.model.bo.OrganizationModuleBO;
import com.zhs.model.bo.OrganizationTagBO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/27 16:36
 */
@ApiModel("组织")
@Data
public class OrganizationVO {
    private Organization organization;

    private List<OrganizationTagBO> tags;

    private List<OrganizationModuleBO> organizationModules;
}
