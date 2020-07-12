package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.mapper.ContactsMapper;
import com.zhs.backmanageb.service.ContactsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * <p>
 * 联系人 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class ContactsServiceImpl extends ServiceImpl<ContactsMapper, Contacts> implements ContactsService {

    @Autowired
    private ContactsMapper contactsMapper;

}
