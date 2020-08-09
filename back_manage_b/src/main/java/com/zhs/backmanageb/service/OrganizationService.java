package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.Organization;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.model.bo.OrganizationTagBO;
import com.zhs.backmanageb.model.vo.OrganizationVO;

import java.util.List;

/**
 * <p>
 * 组织机构 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface OrganizationService extends IService<Organization> {

    OrganizationVO queryByOrganizationType(Long organizationTypeId, Long areaId);

    OrganizationVO queryByParentId(Long id);

    void dealTags(Long id, List<OrganizationTagBO> tags);
}
