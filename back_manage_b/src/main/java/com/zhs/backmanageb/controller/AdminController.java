package com.zhs.backmanageb.controller;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Page;
import com.zhs.backmanageb.entity.Role;
import com.zhs.backmanageb.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: zhs
 * @date: 2020/6/9 18:57
 */
@RequestMapping("/admin")
@RestController
@Api(tags = "管理员管理")
public class AdminController {

    @Resource
    private AdminService adminService;

    @PostMapping("/login")
    @ApiOperation("登陆")
    public Result<String> login(String username,String password){
        System.out.println("login");

        return Result.success("");
    }
    @PostMapping("logout")
    @ApiOperation("退出")
    public Result<Boolean> logout(Long adminId){

        return Result.success(true);
    }
    @ApiOperation("根据管理员id查询拥有角色")
    @PostMapping("list_role/by_admin_id")
    public Result<List<Role>> listRoleByAdminId(@RequestParam Long adminId){
        return Result.success(adminService.listRoleByAdminId(adminId));
    }

    @PostMapping("list_page/by_admin_id")
    @ApiOperation("根据管理员id查询拥有页面权限")
    public Result<List<Page>> listPageByAdminId(@RequestParam Long adminId){
        return Result.success(adminService.listPageByAdminId(adminId));
    }

    @PostMapping("list_page/by_role_id")
    @ApiOperation("根据角色id查询页面权限")
    public Result<List<Page>> listPageByRoleId(@RequestParam Long roleId){
        return Result.success(adminService.listPageByRoleId(roleId));
    }

}
