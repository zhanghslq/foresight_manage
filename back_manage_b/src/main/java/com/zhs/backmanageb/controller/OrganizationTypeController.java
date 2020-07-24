package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

    @ApiOperation("添加组织类别")
    @PostMapping("/insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "detail",value = "分类组织备注",required = false),
            @ApiImplicitParam(name = "name",value = "组织分类名称",required = true),
            @ApiImplicitParam(name = "parentId",value = "上级分类id",required = true),
            @ApiImplicitParam(name = "type",value = "所属分类（军，政，等）",required = true),
    })
    public Result<Boolean> insert(String detail,@RequestParam String name,@RequestParam Long parentId,@RequestParam Integer type){
        OrganizationType organizationType = new OrganizationType();
        organizationType.setDetail(detail);
        organizationType.setName(name);
        organizationType.setParentId(parentId);
        organizationType.setType(type);
        organizationTypeService.save(organizationType);
        return Result.success(true);
    }
    @ApiOperation("添加组织类别并批量添加子类别")
    @PostMapping("/insertBatch")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content",value = "分类组织详情，用于批量添加子类别，一行一个",required = false),
            @ApiImplicitParam(name = "name",value = "组织分类名称",required = true),
            @ApiImplicitParam(name = "parentId",value = "上级分类id",required = true),
            @ApiImplicitParam(name = "type",value = "所属分类（军，政，等）",required = true),
    })
    public Result<Boolean> insertBatch(String content,@RequestParam String name,@RequestParam Long parentId,@RequestParam Integer type){
        OrganizationType organizationType = new OrganizationType();
        organizationType.setName(name);
        organizationType.setParentId(parentId);
        organizationType.setType(type);
        organizationTypeService.save(organizationType);
        if(!StringUtils.isEmpty(content)){
            organizationTypeService.insertBatch(content,type,organizationType.getId());
        }
        return Result.success(true);
    }

    @PostMapping("/list")
    @ApiOperation("全部组织")
    public Result<List<OrganizationType>> list(){
        List<OrganizationType> list = organizationTypeService.list();
        return Result.success(list);
    }
    @PostMapping("/listData/byType")
    @ApiOperation("根据组织类别查询一级类别")
    @ApiImplicitParam(name = "type",value = "类别",required = true)
    public Result<List<OrganizationType>> list(@RequestParam Integer type){
        QueryWrapper<OrganizationType> wrapper = new QueryWrapper<>();
        wrapper.eq("type",type);
        wrapper.eq("parent_id",0);
        List<OrganizationType> list = organizationTypeService.list(wrapper);
        return Result.success(list);
    }
    @PostMapping("/list/type")
    @ApiOperation("军，政，法，等以及对应的编号")
    public Result<List<CommonTypeVO>> listType(){
        List<CommonTypeVO> commonTypeVOS = organizationTypeService.listType();
        return Result.success(commonTypeVOS);
    }
    @PostMapping("/list/all")
    @ApiOperation("拿到所有数据，体系以及体系下的组织(平铺结构)")
    public Result<List<OrganizationTypeBO>> listAll(){
        List<OrganizationTypeBO> commonTypeVOS =organizationTypeService.listAll();
        return Result.success(commonTypeVOS);
    }

    @PostMapping("/list/allTree")
    @ApiOperation("拿到所有数据，体系以及体系下的组织(树状结构)")
    public Result<List<CommonTypeVO>> listAllTree(){
        List<CommonTypeVO> commonTypeVOS =organizationTypeService.listAllTree();
        return Result.success(commonTypeVOS);
    }
    @PostMapping("list/byParentId")
    @ApiOperation("根据id，查找直系下属的组织，查最顶级的传0")
    public Result<List<OrganizationType>> listByParentId(@RequestParam Long parentId){
        QueryWrapper<OrganizationType> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",parentId);
        List<OrganizationType> list = organizationTypeService.list(wrapper);
        return Result.success(list);
    }

    /*@PostMapping("")
    @ApiOperation("根据id，查找全部下属的组织树，查最顶级的传0")
    public Result<List<OrganizationType>> listAllChildrenByParentId(@RequestParam Long parentId){
        QueryWrapper<OrganizationType> wrapper = new QueryWrapper<>();

        List<OrganizationType> list = organizationTypeService.list(wrapper);
        return Result.success(list);
    }*/

}

