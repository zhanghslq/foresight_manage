package com.zhs.model.vo;

import com.zhs.entity.Company;
import lombok.Data;

import java.util.List;

/**
 * 一个类别下的企业
 * @author: zhs
 * @since: 2020/11/23 11:43
 */
@Data
public class CompanyTypeVO {
    private Long companyTypeId;
    private String companyTypeName;
    private List<Company> companyList;
}
