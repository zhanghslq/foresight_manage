package com.zhs.mapper;

import com.zhs.entity.Company;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.OrganizationHasParentBO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 企业 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface CompanyMapper extends BaseMapper<Company> {

    OrganizationHasParentBO listParentById(@Param("organizationId") Long organizationId);
}
