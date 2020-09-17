package com.zhs.controller.b.back;


import cn.hutool.core.bean.BeanUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.AdminRole;
import com.zhs.entity.Role;
import com.zhs.model.vo.RoleVO;
import com.zhs.service.AdminRoleService;
import com.zhs.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @Resource
    private AdminRoleService adminRoleService;


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
    public Result<List<RoleVO>> list(){
        List<RoleVO> result = new ArrayList<>();
        List<Role> roleList = roleService.list();
        if(roleList.size()>0){
            List<AdminRole> list = adminRoleService.list();
            Map<Long, List<AdminRole>> roleMap = list.stream().collect(Collectors.groupingBy(AdminRole::getRoleId));
            for (Role role : roleList) {
                RoleVO roleVO = new RoleVO();
                BeanUtil.copyProperties(role,roleVO);
                List<AdminRole> adminRoles = roleMap.get(role.getId());
                if(!Objects.isNull(adminRoles)){
                    roleVO.setAdminCount(adminRoles.size());
                }
                result.add(roleVO);
            }
        }
        return Result.success(result);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除角色",tags = "删除")
    public Result<Boolean> delete(Long id){
        return Result.success(roleService.removeById(id));
    }

}

