package com.zhs.backmanageb.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.entity.Admin;
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

    Admin login(String username, String password);

    Admin queryByUserName(String username);

    Admin queryByMobile(String mobile);

    void register(String username, String password, String realName, String mobile, Long roleId);

    void updateUserAndRole(Long adminId, String username, String password, String realName, String mobile, Long roleId);

    void updatePassword(Long adminId, String password);
}
