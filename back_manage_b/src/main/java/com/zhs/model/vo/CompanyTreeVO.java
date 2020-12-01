package com.zhs.model.vo;

import com.zhs.model.bo.CompanyTreeBO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/12/1 11:31
 */
@Data
public class CompanyTreeVO {
    private Long companyId;
    /**
     * 企业名称
     */
    private String companyName;
    /**
     * 企业的子企业，按照上下级关系类型分类
     */
    private List<CompanyTreeBO> companyTreeBOList;

}
