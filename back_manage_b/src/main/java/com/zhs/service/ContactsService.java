package com.zhs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.entity.Contacts;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.bo.LongBO;

import java.util.List;

/**
 * <p>
 * 联系人 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ContactsService extends IService<Contacts> {

    void saveBatchSelf(List<Contacts> readBooks);

    void bindingOrganization(Integer isCompany, Long organizationId, List<Long> contactIds);

    Page<Contacts> listContactExpert(Long adminId, Integer current, Integer size);

    List<LongBO> countByOrganizationId(List<Long> organizationIdList);

}
