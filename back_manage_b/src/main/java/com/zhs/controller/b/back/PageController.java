package com.zhs.controller.b.back;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Page;
import com.zhs.model.bo.PageBO;
import com.zhs.service.PageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 页面表 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
@Api(tags = "页面管理")
@RestController
@RequestMapping("/page")
public class PageController {
    @Resource
    private PageService pageService;

    @PostMapping("insert")
    @ApiOperation(value = "插入页面",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(Page page){
        return Result.success(pageService.save(page));
    }

    @ApiOperation(value = "查询所有的页面",tags = "查询")
    @PostMapping("list")
    public Result<List<Page>> list(){
        return Result.success(pageService.list());
    }


    @PostMapping("list/tree")
    @ApiOperation(value = "查询所有页面树状结构",tags = "查询")
    public Result<List<PageBO>> listTree(){
        return Result.success(pageService.listTree());
    }


    @PostMapping("update")
    @ApiOperation(value = "修改页面属性",tags = "修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(Page page){
        return Result.success(pageService.updateById(page));
    }

    @ApiOperation(value = "删除页面",tags = "删除")
    @PostMapping("delete")
    @ApiImplicitParam(name = "id",value = "页面id",required = true)
    public Result<Boolean> delete(Long id){
        return Result.success(pageService.removeById(id));
    }

}

