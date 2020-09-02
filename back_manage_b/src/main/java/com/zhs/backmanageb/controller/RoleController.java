package com.zhs.backmanageb.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Role;
import com.zhs.backmanageb.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 角色 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    private RoleService roleService;


    @PostMapping("insert")
    @ApiOperation(value = "插入角色",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(Role role){
        return Result.success(roleService.save(role));
    }

    @PostMapping("update")
    @ApiOperation(value = "修改角色",tags = "修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(Role role){
        return Result.success(roleService.updateById(role));
    }

    @PostMapping("query")
    @ApiOperation(value = "根据id查询角色详情",tags = "查询")
    public Result<Role> query(Long id){
        return Result.success(roleService.getById(id));
    }

    @PostMapping("list")
    @ApiOperation(value = "查询全部角色",tags = "查询")
    public Result<List<Role>> list(){
        return Result.success(roleService.list());
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除角色",tags = "删除")
    public Result<Boolean> delete(Long id){
        return Result.success(roleService.removeById(id));
    }

}

