package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.OrganizationType;
import com.zhs.backmanageb.model.bo.OrganizationTypeBO;
import com.zhs.backmanageb.model.vo.CommonTypeVO;
import com.zhs.backmanageb.model.vo.OrganizationTypeInsertVO;
import com.zhs.backmanageb.service.OrganizationTypeService;
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
import javax.naming.Name;
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
@RequestMapping("/organizationType")
@Api(tags = "组织类别管理")
public class OrganizationTypeController {
    @Resource
    private OrganizationTypeService organizationTypeService;

    @ApiOperation(value = "添加组织类别",tags = "新增")
    @PostMapping("/insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "detail",value = "分类组织备注"),
            @ApiImplicitParam(name = "name",value = "组织分类名称",required = true),
            @ApiImplicitParam(name = "parentId",value = "上级分类id",required = true),
            @ApiImplicitParam(name = "type",value = "所属分类（军，政，等）",required = true),
            @ApiImplicitParam(name = "hasLocation",value = "是否需要关联地区，0否，1是",required = true)
    })
    public Result<Boolean> insert(@RequestParam Integer hasLocation,String detail,@RequestParam String name,@RequestParam Long parentId,@RequestParam Integer type){
        OrganizationType organizationType = new OrganizationType();
        organizationType.setDetail(detail);
        organizationType.setName(name);
        organizationType.setParentId(parentId);
        organizationType.setType(type);
        organizationType.setHasLocation(hasLocation);
        organizationTypeService.save(organizationType);
        return Result.success(true);
    }
    @ApiOperation(value = "添加组织类别并批量添加子类别",tags = "新增")
    @PostMapping("/insertBatch")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content",value = "分类组织详情，用于批量添加子类别，一行一个",required = false),
            @ApiImplicitParam(name = "name",value = "组织分类名称",required = true),
            @ApiImplicitParam(name = "parentId",value = "上级分类id",required = true),
            @ApiImplicitParam(name = "type",value = "所属分类（军，政，等）",required = true),
            @ApiImplicitParam(name = "hasLocation",value = "是否需要关联地区，0否，1是",required = true)
    })
    public Result<Boolean> insertBatch(@RequestParam Integer hasLocation,String content,@RequestParam String name,@RequestParam Long parentId,@RequestParam Integer type){
        OrganizationType organizationType = new OrganizationType();
        organizationType.setName(name);
        organizationType.setParentId(parentId);
        organizationType.setType(type);
        organizationType.setHasLocation(hasLocation);
        organizationTypeService.save(organizationType);
        if(!StringUtils.isEmpty(content)){
            organizationTypeService.insertBatch(content,type,organizationType.getId(),hasLocation);
        }
        return Result.success(true);
    }

    @PostMapping("/list")
    @ApiOperation(value = "全部组织",tags = "查询")
    public Result<List<OrganizationType>> list(){
        List<OrganizationType> list = organizationTypeService.list();
        return Result.success(list);
    }
    @PostMapping("/listData/byType")
    @ApiOperation(value = "根据组织类别查询一级类别",tags = "查询")
    @ApiImplicitParam(name = "type",value = "类别",required = true)
    public Result<List<OrganizationType>> list(@RequestParam Integer type){
        QueryWrapper<OrganizationType> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("parent_id",0);
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
    public Result<List<CommonTypeVO>> listAllTree(){
        List<CommonTypeVO> commonTypeVOS =organizationTypeService.listAllTree();
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

    @PostMapping("update")
    @ApiOperation(value = "修改",tags = "修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(OrganizationType organizationType){
        return Result.success(organizationTypeService.updateById(organizationType));
    }
    @PostMapping("delete")
    @ApiOperation(value = "删除",tags = "删除")
    @ApiImplicitParam(name = "id",value = "组织类别id",required = true)
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(organizationTypeService.removeById(id));
    }


}

