package com.zhs.backmanageb.controller;


import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.OrganizationModule;
import com.zhs.backmanageb.service.OrganizationModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 组织模块 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-27
 */
@Api(tags = "组织模块")
@RestController
@RequestMapping("/organizationModule")
public class OrganizationModuleController {

    @Resource
    private OrganizationModuleService organizationModuleService;

    @PostMapping("insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId" ,value = "组织id",required = true),
            @ApiImplicitParam(name = "type",value = "模块类型，0领导人，1下属机构，2联系人，3下属企业",required = true),
            @ApiImplicitParam(name = "name",value = "模块名称",required = true)
    })
    @ApiOperation(value = "插入模块，返回模块id",tags = "新增")
    public Result<Long> insert(OrganizationModule organizationModule){
        organizationModuleService.save(organizationModule);
        return Result.success(organizationModule.getId());
    }
}

