package com.zhs.backmanageb.model.dto;

import com.zhs.backmanageb.entity.Company;
import com.zhs.backmanageb.model.bo.OrganizationTagBO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/8/9 14:31
 */
@Data
public class CompanyDTO {
    private Company company;
    List<OrganizationTagBO> tags;
}
