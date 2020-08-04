package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Expert;
import com.zhs.backmanageb.service.ExpertService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 专家 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "专家管理")
@RestController
@RequestMapping("/expert")
public class ExpertController {
    @Resource
    private ExpertService expertService;

    @ApiOperation(value = "获取专家列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    @PostMapping("list")
    public Result<Page<Expert>> list(@RequestParam Integer current,@RequestParam Integer size){
        Page<Expert> expertPage = new Page<>(current, size);
        Page<Expert> page = expertService.page(expertPage);
        return Result.success(page);
    }

    @PostMapping("query")
    @ApiOperation(value = "获取详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<Expert> queryById(@RequestParam Long id){
        Expert expert = expertService.getById(id);
        return Result.success(expert);
    }
    @PostMapping("update")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    @ApiOperation(value = "更新方法",tags = "修改")
    public Result<Boolean> update(Expert expert){
        return Result.success(expertService.updateById(expert));
    }

    @ApiOperation(value = "根据id删除",tags = "删除")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    @PostMapping("delete")
    public Result<Boolean> deleteById(@RequestParam Long id){
        return Result.success(expertService.removeById(id));
    }
    @PostMapping("insert")
    @ApiOperation(value = "插入",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(Expert expert){
        // 插入的时候需要记录操作人id
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            Long adminId = Long.valueOf(principal.toString());
            expert.setAdminId(adminId);
        } catch (NumberFormatException e) {
            expert.setAdminId(0L);
        }

        return Result.success(expertService.save(expert));
    }
}

