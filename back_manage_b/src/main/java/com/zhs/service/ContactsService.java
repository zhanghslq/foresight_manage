package com.zhs.service;

import com.zhs.entity.Contacts;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
