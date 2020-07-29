package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.Company;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.model.vo.CompanyVO;

/**
 * <p>
 * 企业 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface CompanyService extends IService<Company> {

    CompanyVO queryByOrganizationType(Long organizationTypeId, Long areaId);

    CompanyVO queryByParentId(Long id);
}
