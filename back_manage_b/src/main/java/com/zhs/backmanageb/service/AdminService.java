package com.zhs.backmanageb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.entity.Admin;
import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.entity.Page;
import com.zhs.backmanageb.entity.Role;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/11 16:06
 */
public interface AdminService extends IService<Admin> {
    List<Role> listRoleByAdminId(Long adminId);

    List<Page> listPageByAdminId(Long adminId);

    List<Page> listPageByRoleId(Long roleId);
}
