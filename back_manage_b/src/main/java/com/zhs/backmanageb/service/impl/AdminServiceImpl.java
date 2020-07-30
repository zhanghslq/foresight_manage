package com.zhs.backmanageb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.backmanageb.entity.*;
import com.zhs.backmanageb.mapper.AdminMapper;
import com.zhs.backmanageb.mapper.CommonDataMapper;
import com.zhs.backmanageb.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 一些公用数据（下拉框选择的） 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminRoleService adminRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PageService pageService;

    @Autowired
    private RolePageService rolePageService;



    @Override
    public List<Role> listRoleByAdminId(Long adminId) {
        ArrayList<Role> result = new ArrayList<>();
        QueryWrapper<AdminRole> adminRoleQueryWrapper = new QueryWrapper<>();
        adminRoleQueryWrapper.eq("admin_id",adminId);
        List<AdminRole> list = adminRoleService.list(adminRoleQueryWrapper);
        if(list.size()==0){
            return result;
        }
        List<Long> roleIds = list.stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        return roleService.listByIds(roleIds);
    }

    @Override
    public List<Page> listPageByAdminId(Long adminId) {
        ArrayList<Page> result = new ArrayList<>();
        QueryWrapper<AdminRole> adminRoleQueryWrapper = new QueryWrapper<>();
        adminRoleQueryWrapper.eq("admin_id",adminId);
        List<AdminRole> list = adminRoleService.list(adminRoleQueryWrapper);
        if(list.size()==0){
            return result;
        }
        List<Long> roleIds = list.stream().map(AdminRole::getRoleId).collect(Collectors.toList());
        QueryWrapper<RolePage> rolePageQueryWrapper = new QueryWrapper<>();
        rolePageQueryWrapper.in("role_id",roleIds);
        List<RolePage> rolePages = rolePageService.list(rolePageQueryWrapper);
        if(rolePages.size()==0){
            return result;
        }
        List<Long> pageIds = rolePages.stream().map(RolePage::getPageId).collect(Collectors.toList());

        return pageService.listByIds(pageIds);
    }

    @Override
    public List<Page> listPageByRoleId(Long roleId) {
        ArrayList<Page> result = new ArrayList<>();
        QueryWrapper<RolePage> rolePageQueryWrapper = new QueryWrapper<>();
        rolePageQueryWrapper.eq("role_id",roleId);
        List<RolePage> rolePages = rolePageService.list(rolePageQueryWrapper);
        if(rolePages.size()==0){
            return result;
        }
        List<Long> pageIds = rolePages.stream().map(RolePage::getPageId).collect(Collectors.toList());
        return pageService.listByIds(pageIds);
    }
}
