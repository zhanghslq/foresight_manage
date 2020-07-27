package com.zhs.backmanageb.model.vo;

import com.zhs.backmanageb.entity.Organization;
import com.zhs.backmanageb.model.bo.OrganizationModuleBO;
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

    private List<OrganizationModuleBO> organizationModules;
}
