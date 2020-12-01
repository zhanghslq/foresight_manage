package com.zhs.model.bo;

import com.zhs.model.vo.CompanyTreeVO;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/12/1 11:36
 */
@Data
public class CompanyTreeBO {
    /**
     * 关系类型
     */
    private String relationshipName;
    /**
     * 关系类型下的企业列表
     */
    private List<CompanyTreeVO> companyTreeVOList;
}
