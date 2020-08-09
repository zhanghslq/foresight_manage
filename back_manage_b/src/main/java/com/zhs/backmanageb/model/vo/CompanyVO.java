package com.zhs.backmanageb.model.vo;

import com.zhs.backmanageb.entity.Company;
import com.zhs.backmanageb.entity.Organization;
import com.zhs.backmanageb.model.bo.CompanyModuleBO;
import com.zhs.backmanageb.model.bo.OrganizationModuleBO;
import com.zhs.backmanageb.model.bo.OrganizationTagBO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/27 16:36
 */
@ApiModel("企业")
@Data
public class CompanyVO {
    private Company company;

    private List<OrganizationTagBO> tags;

    private List<CompanyModuleBO> companyModules;
}
