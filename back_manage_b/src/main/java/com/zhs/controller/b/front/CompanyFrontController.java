package com.zhs.controller.b.front;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Company;
import com.zhs.entity.OrganizationTag;
import com.zhs.entity.OrganizationType;
import com.zhs.model.bo.OrganizationHasParentBO;
import com.zhs.model.dto.CompanyDTO;
import com.zhs.model.vo.CompanyDetailVO;
import com.zhs.model.vo.CompanyModuleVO;
import com.zhs.model.vo.CompanyTypeVO;
import com.zhs.model.vo.CompanyVO;
import com.zhs.service.CompanyService;
import com.zhs.service.OrganizationTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/company_front")
@Api(tags = "企业管理")
public class CompanyFrontController {
    @Resource
    private CompanyService companyService;
    @Resource
    private OrganizationTagService organizationTagService;

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

    @ApiOperation(value = "根据类别查询下面子类别下的企业",tags = "查询")
    @PostMapping("list/by_type")
    public Result<List<CompanyTypeVO>> listByType(@RequestParam Long typeId){

        List<CompanyTypeVO> companyList = companyService.listByType(typeId);
        return Result.success(companyList);
    }

    @ApiOperation(value = "企业的顶级类别",tags = "查询")
    @PostMapping("list/top_type")
    public Result<List<OrganizationType>> listTopOrganizationType(){
        List<OrganizationType> list = companyService.listTopOrganizationType();
        return Result.success(list);
    }

    @ApiOperation(value = "根据企业id查询详细信息（右边栏）",tags = "查询")
    @PostMapping("get/by_id")
    public Result<CompanyDetailVO> getDetailById(@RequestParam Long companyId){
        CompanyDetailVO companyDetailVO = companyService.getDetailById(companyId);
        return Result.success(companyDetailVO);
    }

    @ApiOperation(value = "地区国企检索",tags = "查询")
    @PostMapping("list/by_area")
    public Result<Object> listByArea(Long regionId, Long provinceId, Long cityId){
        List<CompanyModuleVO> list = companyService.listByRegionProvinceCityId(regionId,provinceId,cityId);
        return Result.success(list);
    }


}

