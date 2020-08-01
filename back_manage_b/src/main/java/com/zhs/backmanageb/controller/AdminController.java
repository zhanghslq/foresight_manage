package com.zhs.backmanageb.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Admin;
import com.zhs.backmanageb.entity.Page;
import com.zhs.backmanageb.entity.Role;
import com.zhs.backmanageb.model.dto.AdminLoginReturnDTO;
import com.zhs.backmanageb.model.dto.AdminVO;
import com.zhs.backmanageb.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
    public Result<AdminLoginReturnDTO> login(HttpServletRequest request, String username, String password){
        Admin admin = adminService.login(username,password);
        String sessionId = request.getSession().getId();
        AdminLoginReturnDTO adminLoginReturnDTO = new AdminLoginReturnDTO();
        BeanUtil.copyProperties(admin,adminLoginReturnDTO);
        adminLoginReturnDTO.setToken(sessionId);
        return Result.success(adminLoginReturnDTO);
    }
    @PostMapping("logout")
    @ApiOperation("退出")
    public Result<Boolean> logout(Long adminId){
        SecurityUtils.getSubject().logout();
        return Result.success(true);
    }
    @PostMapping("add")
    @ApiOperation("添加用户")
    public Result<Boolean> add(String username,String password,String realName){
        adminService.register(username,password,realName);
        return Result.success(true);
    }

    @PostMapping("updatePassword")
    @ApiOperation("修改密码")
    public Result<Boolean> updatePassword(){

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

    @PostMapping("list")
    @ApiOperation("查询所有用户")
    public Result<List<AdminVO>> list(){
        List<Admin> list = adminService.list();
        List<AdminVO> result = new ArrayList<>();
        for (Admin admin : list) {
            AdminVO adminVO = new AdminVO();
            BeanUtil.copyProperties(admin,adminVO);
            result.add(adminVO);
        }
        return Result.success(result);
    }

    @PostMapping("list/by_page")
    @ApiOperation("分页查询所有用户")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<AdminVO>> listByPage(@RequestParam Integer current, @RequestParam Integer size){
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Admin> adminPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Admin> page = adminService.page(adminPage);
        List<Admin> records = page.getRecords();
        List<AdminVO> result = new ArrayList<>();
        for (Admin admin : records) {
            AdminVO adminVO = new AdminVO();
            BeanUtil.copyProperties(admin,adminVO);
            result.add(adminVO);
        }
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<AdminVO> adminVOPage = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        BeanUtil.copyProperties(page,adminVOPage);
        adminVOPage.setRecords(result);
        return Result.success(adminVOPage);
    }
}
