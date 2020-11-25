package com.zhs.model.vo;

import com.zhs.entity.Company;
import lombok.Data;
import org.aspectj.lang.annotation.DeclareAnnotation;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/11/25 17:25
 */
@Data
public class CompanyModuleVO {
    private Long moduleId;
    private String moduleName;
    private List<Company> companyList;
}
