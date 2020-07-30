package com.zhs.backmanageb.controller;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.AdminRole;
import com.zhs.backmanageb.service.AdminRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
@Api(tags = "用户角色管理")
@RestController
@RequestMapping("/adminRole")
public class AdminRoleController {

    @Resource
    private AdminRoleService adminRoleService;


    @PostMapping("insert")
    @ApiOperation("给用户增加角色")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(AdminRole adminRole){
        return Result.success(adminRoleService.save(adminRole));
    }

    @PostMapping("delete")
    @ApiOperation("根据id删除")
    public Result<Boolean> delete(Long id){
        return Result.success(adminRoleService.removeById(id));
    }

    @PostMapping("insertBatch")
    @ApiOperation("给用户添加多个角色")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleIds",value = "角色id，多个用逗号相隔",required = true),
            @ApiImplicitParam(name = "adminId",value = "管理员id",required = true),

    })
    public Result<Boolean> insertBatch(@RequestParam List<Long> roleIds,@RequestParam Long adminId){
        Assert.notEmpty(roleIds,"角色不能为空");
        List<AdminRole> adminRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            adminRoles.add(adminRole);
        }
        return Result.success(adminRoleService.saveBatch(adminRoles));
    }

    // 查一个用户的所有角色


}

