package com.zhs.backmanageb.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Company;
import com.zhs.backmanageb.model.vo.CompanyVO;
import com.zhs.backmanageb.model.vo.OrganizationVO;
import com.zhs.backmanageb.service.CompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 企业 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@RestController
@RequestMapping("/company")
@Api(tags = "企业管理")
public class CompanyController {
    @Resource
    private CompanyService companyService;

    @PostMapping("insert")
    @ApiOperation("插入企业")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(@RequestBody Company company){
        return Result.success(companyService.save(company));
    }

    @PostMapping("update")
    @ApiOperation("修改企业")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(@RequestBody Company company){
        return Result.success(companyService.updateById(company));
    }

    @PostMapping("delete")
    @ApiOperation("删除企业")
    @ApiImplicitParam(name = "id",value = "企业id",required = true)
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(companyService.removeById(id));
    }

    @PostMapping("query/by_organization_type")
    @ApiOperation("根据组织类别,地区（可选）查询组织,领导人，联系人")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationTypeId",value = "组织类型id",required = true),
            @ApiImplicitParam(name = "areaId",value = "地区id"),
    })
    public Result<CompanyVO> queryByOrganizationType(@RequestParam Long organizationTypeId, Long areaId){
        //
        CompanyVO companyVO = companyService.queryByOrganizationType(organizationTypeId,areaId);
        return Result.success(companyVO);
    }

    @ApiOperation("根据企业Id查询下属企业,领导人，联系人")
    @PostMapping("query/by_id")
    @ApiImplicitParam(name = "id",value = "企业id",required = true)
    public Result<CompanyVO> queryById(@RequestParam Long id){
        CompanyVO companyVO = companyService.queryByParentId(id);
        return Result.success(companyVO);
    }

}

