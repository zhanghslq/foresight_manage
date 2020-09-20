package com.zhs.controller.b.back;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Organization;
import com.zhs.entity.OrganizationTag;
import com.zhs.model.bo.OrganizationHasParentBO;
import com.zhs.model.dto.OrganizationDTO;
import com.zhs.model.vo.OrganizationVO;
import com.zhs.service.OrganizationService;
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
 * 组织机构 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "组织管理")
@RestController
@RequestMapping("/organization")
public class OrganizationController {

    @Resource
    private OrganizationService organizationService;
    @Resource
    private OrganizationTagService organizationTagService;


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
    @PostMapping("insert")
    @ApiOperation(value = "插入组织",tags = "新增")
    @ApiImplicitParam(name = "tags",value = "标签,多个逗号相隔")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Long> insert(@RequestBody Organization organization,@RequestParam(value = "tags",required = false) List<String> tags){
        // 插入的时候需要记录操作人id
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            Long adminId = Long.valueOf(principal.toString());
            organization.setAdminId(adminId);
        } catch (NumberFormatException e) {
            organization.setAdminId(0L);
        }
        organizationService.save(organization);
        if(!Objects.isNull(tags)&&tags.size()>0){
            ArrayList<OrganizationTag> organizationTags = new ArrayList<>();
            for (String tag : tags) {
                OrganizationTag organizationTag = new OrganizationTag();
                organizationTag.setIsCompany(0);
                organizationTag.setName(tag);
                organizationTag.setOrganizationId(organization.getId());
                organizationTags.add(organizationTag);
            }
            organizationTagService.saveBatch(organizationTags);
        }
        return Result.success(organization.getId());
    }

    @PostMapping("update")
    @ApiOperation(value = "修改组织",tags = "修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(@RequestBody OrganizationDTO organizationDTO){
        organizationService.dealTags(organizationDTO.getOrganization().getId(),organizationDTO.getTags());
        return Result.success(organizationService.updateById(organizationDTO.getOrganization()));
    }

    // 查一个组织的时候会查组织详情和领导人，以及联系人


    @PostMapping("query/by_organization_type")
    @ApiOperation(value = "根据组织类别,地区（可选）查询组织,领导人，联系人（企业查询除外，企业是根这些分开的）",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationTypeId",value = "组织类型id",required = true),
            @ApiImplicitParam(name = "areaId",value = "地区id"),
    })
    public Result<OrganizationVO> queryByOrganizationType(@RequestParam Long organizationTypeId, Long areaId){
        //
        OrganizationVO organizationVO = organizationService.queryByOrganizationType(organizationTypeId,areaId);
        return Result.success(organizationVO);
    }

    @ApiOperation(value = "根据组织Id查询下属组织,领导人，联系人（企业查询除外，企业是根这些分开的）",tags = "查询")
    @PostMapping("query/by_id")
    @ApiImplicitParam(name = "id",value = "组织id",required = true)
    public Result<OrganizationVO> queryById(@RequestParam Long id){
        OrganizationVO organizationVO = organizationService.queryByParentId(id);
        return Result.success(organizationVO);
    }
    @ApiOperation(value = "删除组织",tags = "删除")
    @PostMapping("delete")
    @ApiImplicitParam(name = "id",value = "组织id",required = true)
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(organizationService.removeById(id));
    }

    @ApiOperation(value = "根据组织id查所有父类",tags = "查询")
    @PostMapping("list_parent/by_organization_id")
    public Result<OrganizationHasParentBO> listParentById(@RequestParam Long organizationId){
        OrganizationHasParentBO organizationHasParentBO = organizationService.listParentById(organizationId);
        return Result.success(organizationHasParentBO);
    }

    @ApiOperation(value = "上传文件进行批量插入组织",tags = "新增")
    @PostMapping("listUpload")
    public Result<Boolean> listUpload(@RequestParam Long moduleId,@RequestParam("file") MultipartFile file){
        organizationService.listUpload(moduleId,file);
        return Result.success(true);
    }
}
