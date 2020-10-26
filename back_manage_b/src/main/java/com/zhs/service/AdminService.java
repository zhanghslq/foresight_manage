package com.zhs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.entity.Admin;
import com.zhs.entity.Page;
import com.zhs.entity.Role;
import com.zhs.model.dto.AdminVO;
import com.zhs.model.vo.AdminAddDataVO;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/11 16:06
 */
public interface AdminService extends IService<Admin> {
    List<Role> listRoleByAdminId(Long adminId);

    List<Page> listPageByAdminId(Long adminId);

    List<Page> listPageByRoleId(Long roleId);

    Admin login(String username, String password, Integer type);

    Admin queryByUserName(String username);

    Admin queryByMobile(String mobile);

    void register(String username, String password, String realName, String mobile, Long roleId, Integer type);

    void updateUserAndRole(Long adminId, String username, String realName, String mobile, Long roleId);

    void updatePassword(Long adminId,  String password);

    AdminAddDataVO queryAddData(Long adminId);

    void addOnLineTime(Long adminId);

    void addOperatorCount(Long adminId);

    com.baomidou.mybatisplus.extension.plugins.pagination.Page<AdminVO> pageSelf(Integer current, Integer size);
}
