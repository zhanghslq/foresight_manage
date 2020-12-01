package com.zhs.controller.b.front;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Organization;
import com.zhs.entity.OrganizationTag;
import com.zhs.entity.OrganizationType;
import com.zhs.model.bo.OrganizationHasParentBO;
import com.zhs.model.dto.OrganizationDTO;
import com.zhs.model.vo.*;
import com.zhs.service.OrganizationService;
import com.zhs.service.OrganizationTagService;
import com.zhs.service.OrganizationTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.crypto.interfaces.PBEKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织机构 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "组织管理")
@RestController
@RequestMapping("/organization_front")
public class OrganizationFrontController {

    @Resource
    private OrganizationService organizationService;
    @Resource
    private OrganizationTagService organizationTagService;
    @Resource
    private OrganizationTypeService organizationTypeService;


    @ApiOperation(value = "根据类型获取组织列表（军，政，法等）",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type",value = "类型，（准备获取哪种组织的列表）",required = true),
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    @PostMapping("/list/by_type")
    public Result<Page<Organization>> listByType(@RequestParam Integer type, @RequestParam Integer current, @RequestParam Integer size){
        Page<Organization> organizationPage = new Page<>(current, size);
        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("type",type);
        Page<Organization> page = organizationService.page(organizationPage, organizationQueryWrapper);
        return Result.success(page);
    }

    // 查一个组织的时候会查组织详情和领导人，以及联系人


    @PostMapping("query/by_organization_type")
    @ApiOperation(value = "根据组织类别,地区（可选）查询组织,领导人，联系人（企业查询除外，企业是根这些分开的）",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationTypeId",value = "组织类型id",required = true),
            @ApiImplicitParam(name = "areaId",value = "地区id"),
    })
    public Result<OrganizationFrontVO> queryByOrganizationType(@RequestParam Long organizationTypeId, Long areaId){
        //
        OrganizationFrontVO organizationVO = organizationService.queryFrontByOrganizationType(organizationTypeId,areaId);
        return Result.success(organizationVO);
    }

    @ApiOperation(value = "根据组织Id查询下属模块组织信息）",tags = "查询")
    @PostMapping("query/by_id")
    @ApiImplicitParam(name = "id",value = "组织id",required = true)
    public Result<OrganizationFrontVO> queryById(@RequestParam Long id){
        OrganizationFrontVO organizationVO = organizationService.queryFrontByParentId(id);
        return Result.success(organizationVO);
    }

    @ApiOperation(value = "根据组织id查所有父类",tags = "查询")
    @PostMapping("list_parent/by_organization_id")
    public Result<OrganizationHasParentBO> listParentById(@RequestParam Long organizationId){
        OrganizationHasParentBO organizationHasParentBO = organizationService.listParentById(organizationId);
        return Result.success(organizationHasParentBO);
    }
    @ApiOperation(value = "根据机构id，获取总览信息",tags = "查询")
    @PostMapping("query_information/by_id")
    public Result<OrganizationInformationVO> queryInformationById(@RequestParam Long id){
        OrganizationInformationVO organizationInformationVO = organizationService.queryInformationById(id);
        return Result.success(organizationInformationVO);
    }
    @PostMapping("query/by_region_province_city")
    @ApiOperation(value = "根据地区查询机构信息",tags = "查询")
    public Result<List<OrganizationRegionDataVO>> queryByRegionAndProvinceCity(Long regionId, Long provinceId, Long cityId){
        List<OrganizationRegionDataVO> result = organizationService.listByRegionProvinceCityId(regionId,provinceId,cityId);
        return Result.success(result);
    }
    @PostMapping("list/by_area_id")
    @ApiOperation(value = "根据地区id查各个机构类型下最顶级机构",tags = "查询")
    public Result<List<Organization>> listByAreaId(@RequestParam Integer areaId){
        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("area_id",areaId);
        organizationQueryWrapper.eq("parent_id",0);
        organizationQueryWrapper.isNotNull("organization_type_id");
        List<Organization> list = organizationService.list(organizationQueryWrapper);
        List<Long> organizationTypeIdList = list.stream().filter(organization -> Objects.nonNull(organization.getOrganizationTypeId()))
                .map(Organization::getOrganizationTypeId).collect(Collectors.toList());
        List<OrganizationType> organizationTypes = new ArrayList<>();
        if(organizationTypeIdList.size()>0){
            organizationTypes = organizationTypeService.listByIds(organizationTypeIdList);
        }
        List<Long> hasLocationTypeIdList = organizationTypes.stream().filter(organizationType -> Objects.nonNull(organizationType.getHasLocation()) && organizationType.getHasLocation() == 1)
                .map(OrganizationType::getId).collect(Collectors.toList());
        list.removeIf(organization -> !hasLocationTypeIdList.contains(organization.getOrganizationTypeId()));
        return Result.success(list);
    }

    @ApiOperation(value = "根据标签查询机构",tags = "查询")
    @PostMapping("list/by_tag")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagName",value = "标签名称"),
            @ApiImplicitParam(name = "areaId",value = "地区id")
    })
    public Result<List<OrganizationSearchVO>> listByTag(String tagName,String organizationName,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")Date createTimeBegin,
                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date createTimeEnd,@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")Date updateTime,Long areaId){
        List<OrganizationSearchVO> result = organizationService.listByTag(tagName,organizationName,createTimeBegin,createTimeEnd,updateTime,areaId);
        return Result.success(result);
    }

}

