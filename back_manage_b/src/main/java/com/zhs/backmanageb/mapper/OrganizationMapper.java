package com.zhs.backmanageb.mapper;

import com.zhs.backmanageb.entity.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.backmanageb.model.bo.OrganizationHasParentBO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 组织机构 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface OrganizationMapper extends BaseMapper<Organization> {

    OrganizationHasParentBO listParentById(@Param("organizationId") Long organizationId);
}
