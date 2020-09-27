package com.zhs.controller.b.back;


import com.zhs.common.Result;
import com.zhs.entity.RootType;
import com.zhs.service.RootTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 根的类别，机构分类，和企业 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-09-25
 */
@RestController
@RequestMapping("/rootType")
@Api(tags = "体系类别(一级的体系，企业，党务同级别的)")
public class RootTypeController {
    @Autowired
    private RootTypeService rootTypeService;

    @PostMapping("add")
    @ApiOperation(value = "添加体系",tags = "新增")
    @ApiImplicitParam(name = "name",value = "体系名称",required = true)
    public Result<Boolean> add(@RequestParam String name){
        RootType rootType = new RootType();
        rootType.setName(name);
        rootTypeService.save(rootType);
        return Result.success(true);
    }

    @PostMapping("update")
    @ApiOperation(value = "修改体系",tags = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "体系id",required = true),
            @ApiImplicitParam(name = "name",value = "体系名称",required = true),

    })
    public Result<Boolean> update(@RequestParam Integer id,@RequestParam String name){
        RootType rootType = new RootType();
        rootType.setId(id);
        rootType.setName(name);
        rootTypeService.updateById(rootType);
        return Result.success(true);
    }

    @PostMapping("list")
    @ApiOperation(value = "查询全部一级菜单",tags = "查询")
    public Result<List<RootType>> list(){
        return Result.success(rootTypeService.list());
    }

    @PostMapping("delete")
    @ApiOperation(value = "根据id删除",tags = "删除")
    public Result<Boolean> delete(@RequestParam Long id){
        Assert.isTrue(id>6,"初始化的体系不允许删除");
        return Result.success(rootTypeService.removeById(id));
    }

}

