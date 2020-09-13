package com.zhs.model.vo;

import com.zhs.entity.Company;
import com.zhs.model.bo.CompanyModuleBO;
import com.zhs.model.bo.OrganizationTagBO;
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
