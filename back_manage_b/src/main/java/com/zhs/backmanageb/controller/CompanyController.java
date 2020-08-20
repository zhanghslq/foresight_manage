package com.zhs.backmanageb.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Company;
import com.zhs.backmanageb.entity.OrganizationTag;
import com.zhs.backmanageb.model.bo.OrganizationHasParentBO;
import com.zhs.backmanageb.model.bo.OrganizationTagBO;
import com.zhs.backmanageb.model.dto.CompanyDTO;
import com.zhs.backmanageb.model.vo.CompanyVO;
import com.zhs.backmanageb.model.vo.OrganizationVO;
import com.zhs.backmanageb.service.CompanyService;
import com.zhs.backmanageb.service.OrganizationTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @Resource
    private OrganizationTagService organizationTagService;

    @PostMapping("insert")
    @ApiOperation(value = "插入企业",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Long> insert(@RequestBody Company company,@RequestParam(value = "tags",required = false) List<String> tags){
        // 插入的时候需要记录操作人id
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            Long adminId = Long.valueOf(principal.toString());
            company.setAdminId(adminId);
        } catch (NumberFormatException e) {
            company.setAdminId(0L);
        }
        companyService.save(company);
        if(!Objects.isNull(tags)&&tags.size()>0){
            ArrayList<OrganizationTag> organizationTags = new ArrayList<>();
            for (String tag : tags) {
                OrganizationTag organizationTag = new OrganizationTag();
                organizationTag.setIsCompany(1);
                organizationTag.setName(tag);
                organizationTag.setOrganizationId(company.getId());
                organizationTags.add(organizationTag);
            }
            organizationTagService.saveBatch(organizationTags);
        }
        return Result.success(company.getId());
    }

    @PostMapping("update")
    @ApiOperation(value = "修改企业",tags = "修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(@RequestBody CompanyDTO companyDTO){
        companyService.dealTags(companyDTO.getCompany().getId(),companyDTO.getTags());

        return Result.success(companyService.updateById(companyDTO.getCompany()));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除企业",tags = "删除")
    @ApiImplicitParam(name = "id",value = "企业id",required = true)
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(companyService.removeById(id));
    }

    @PostMapping("query/by_organization_type")
    @ApiOperation(value = "根据组织类别,地区（可选）查询组织,领导人，联系人",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationTypeId",value = "组织类型id",required = true),
            @ApiImplicitParam(name = "areaId",value = "地区id"),
    })
    public Result<CompanyVO> queryByOrganizationType(@RequestParam Long organizationTypeId, Long areaId){
        //
        CompanyVO companyVO = companyService.queryByOrganizationType(organizationTypeId,areaId);
        return Result.success(companyVO);
    }

    @ApiOperation(value = "根据企业Id查询下属企业,领导人，联系人",tags = "查询")
    @PostMapping("query/by_id")
    @ApiImplicitParam(name = "id",value = "企业id",required = true)
    public Result<CompanyVO> queryById(@RequestParam Long id){
        CompanyVO companyVO = companyService.queryByParentId(id);
        return Result.success(companyVO);
    }

    @ApiOperation(value = "根据组织id查所有父类",tags = "查询")
    @PostMapping("list_parent/by_organization_id")
    public Result<OrganizationHasParentBO> listParentById(@RequestParam Long organizationId){
        OrganizationHasParentBO organizationHasParentBO = companyService.listParentById(organizationId);
        return Result.success(organizationHasParentBO);
    }

}

