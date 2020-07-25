package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.Organization;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.model.bo.OrganizationBO;

/**
 * <p>
 * 组织机构 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface OrganizationService extends IService<Organization> {

    OrganizationBO queryByOrganizationType(Long organizationTypeId);

    OrganizationBO queryByParentId(Long id);
}
