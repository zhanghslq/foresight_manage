package com.zhs.controller.b.front;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Organization;
import com.zhs.entity.OrganizationType;
import com.zhs.model.bo.OrganizationTypeBO;
import com.zhs.model.vo.CommonTypeVO;
import com.zhs.model.vo.RootTypeVO;
import com.zhs.service.OrganizationTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@RestController
@RequestMapping("/organizationTypeFront")
@Api(tags = "组织类别管理")
public class OrganizationTypeFrontController {
    @Resource
    private OrganizationTypeService organizationTypeService;



    @PostMapping("/list")
    @ApiOperation(value = "全部组织",tags = "查询")
    public Result<List<OrganizationType>> list(){
        QueryWrapper<OrganizationType> organizationTypeQueryWrapper = new QueryWrapper<>();
        organizationTypeQueryWrapper.orderByAsc("seq");
        List<OrganizationType> list = organizationTypeService.list(organizationTypeQueryWrapper);
        return Result.success(list);
    }
    @PostMapping("/listData/byType")
    @ApiOperation(value = "根据组织类别查询一级类别",tags = "查询")
    @ApiImplicitParam(name = "type",value = "类别",required = true)
    public Result<List<OrganizationType>> list(@RequestParam Integer type){
        QueryWrapper<OrganizationType> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("parent_id",0);
        wrapper.orderByAsc("seq");
        List<OrganizationType> list = organizationTypeService.list(wrapper);
        return Result.success(list);
    }

    @PostMapping("/listData/tree/byType")
    @ApiOperation(value = "根据type查询组织类别的树状数据（可用于组织顶部的查询）",tags = "查询")
    @ApiImplicitParam(name = "type",value = "类别",required = true)
    public Result<List<OrganizationTypeBO>> listTreeByType(@RequestParam Integer type){
        List<OrganizationTypeBO> organizationTypeBOS = organizationTypeService.listAllTreeByType(type);
        return Result.success(organizationTypeBOS);
    }
    @PostMapping("/list/type")
    @ApiOperation(value = "军，政，法，等以及对应的编号",tags = "查询")
    public Result<List<CommonTypeVO>> listType(){
        List<CommonTypeVO> commonTypeVOS = organizationTypeService.listType();
        return Result.success(commonTypeVOS);
    }
    @PostMapping("/list/all")
    @ApiOperation(value = "拿到所有数据，体系以及体系下的组织(平铺结构)",tags = "查询")
    public Result<List<OrganizationTypeBO>> listAll(){
        List<OrganizationTypeBO> commonTypeVOS =organizationTypeService.listAll();
        return Result.success(commonTypeVOS);
    }

    @PostMapping("/list/allTree")
    @ApiOperation(value = "拿到所有数据，体系以及体系下的组织(树状结构)",tags = "查询")
    public Result<List<RootTypeVO>> listAllTree(){
        List<RootTypeVO> commonTypeVOS =organizationTypeService.listAllTree();
        return Result.success(commonTypeVOS);
    }
    @PostMapping("list/byParentId")
    @ApiOperation(value = "根据id，查找直系下属的组织，（可用于组织顶部的查询下级组织）",tags = "查询")
    public Result<List<OrganizationType>> listByParentId(@RequestParam Long parentId){
        QueryWrapper<OrganizationType> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",parentId);
        List<OrganizationType> list = organizationTypeService.list(wrapper);
        return Result.success(list);
    }
    @PostMapping("list/son_organization")
    @ApiOperation(value = "查询子类别的机构",tags = "查询")
    public Result<List<Organization>> listSonOrganization(@RequestParam Long organizationTypeId){
        List<Organization> result = organizationTypeService.listSonOrganization(organizationTypeId);
        return Result.success(result);
    }

}

