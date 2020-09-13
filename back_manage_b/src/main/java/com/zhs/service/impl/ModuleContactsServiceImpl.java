package com.zhs.service.impl;

import com.zhs.entity.ModuleContacts;
import com.zhs.mapper.ModuleContactsMapper;
import com.zhs.service.ModuleContactsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 模块-联系人关系表 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-08-11
 */
@Service
public class ModuleContactsServiceImpl extends ServiceImpl<ModuleContactsMapper, ModuleContacts> implements ModuleContactsService {

}
