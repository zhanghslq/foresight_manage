package com.zhs.service.impl;

import com.zhs.entity.Role;
import com.zhs.mapper.RoleMapper;
import com.zhs.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
