package com.zhs.backmanageb.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.injector.methods.UpdateById;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Admin;
import com.zhs.backmanageb.entity.Page;
import com.zhs.backmanageb.entity.Role;
import com.zhs.backmanageb.exception.MyException;
import com.zhs.backmanageb.model.dto.AdminLoginReturnDTO;
import com.zhs.backmanageb.model.dto.AdminVO;
import com.zhs.backmanageb.model.vo.AdminAddDataVO;
import com.zhs.backmanageb.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
import java.util.Objects;

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
    public Result<Boolean> add(@RequestParam String username,@RequestParam String password,String realName,String mobile,Long roleId){
        adminService.register(username,password,realName,mobile,roleId);
        return Result.success(true);
    }

    @PostMapping("update")
    @ApiOperation("修改用户信息以及角色")
    public Result<Boolean> update(@RequestParam Long adminId,@RequestParam String username,@RequestParam String password,String realName,String mobile,Long roleId){
        adminService.updateUserAndRole(adminId,username,password,realName,mobile,roleId);
        return Result.success(true);
    }
    @PostMapping("delete")
    @ApiOperation("删除用户")
    @ApiImplicitParam(name = "adminId",value = "用户id",required = true)
    public Result<Boolean> delete(@RequestParam Long adminId){
        adminService.removeById(adminId);
        return Result.success(true);
    }

    @PostMapping("updatePassword")
    @ApiOperation("修改密码")
    public Result<Boolean> updatePassword(@RequestParam Long adminId,@RequestParam String oldPassword,@RequestParam String password){
        adminService.updatePassword(adminId,oldPassword,password);
        return Result.success(true);
    }
    @PostMapping("freezeUser")
    @ApiOperation("冻结用户")
    public Result<Boolean> freezeUser(@RequestParam Long adminId){
        Admin byId = adminService.getById(adminId);
        if(Objects.isNull(byId)){
            throw new MyException("用户不存在");
        }
        byId.setStatus(1);
        adminService.updateById(byId);
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
            adminVO.setRoleList(adminService.listRoleByAdminId(admin.getId()));
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


    // 查询用户的录入数据
    @PostMapping("query/add_data")
    @ApiOperation("查用户添加的数据")
    public Result<AdminAddDataVO> queryAddData(@RequestParam Long adminId){
        AdminAddDataVO adminAddDataVO = adminService.queryAddData(adminId);
        return Result.success(adminAddDataVO);
    }

}
