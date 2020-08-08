package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.Contacts;
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

}
