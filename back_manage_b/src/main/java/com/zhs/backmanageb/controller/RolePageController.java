package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Page;
import com.zhs.backmanageb.entity.RolePage;
import com.zhs.backmanageb.service.PageService;
import com.zhs.backmanageb.service.RolePageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
@Api(tags = "角色页面管理")
@RestController
@RequestMapping("/rolePage")
public class RolePageController {
    @Resource
    private RolePageService rolePageService;
    @Resource
    private PageService pageService;

    @PostMapping("insert")
    @ApiOperation("增加角色页面权限")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(RolePage rolePage){
        return Result.success(rolePageService.save(rolePage));
    }
    @PostMapping("insertBatch")
    @ApiOperation("批量添加角色页面权限")
    public Result<Boolean> insertBatch(@RequestBody List<RolePage>rolePages){
        return Result.success(rolePageService.saveBatch(rolePages));
    }

    @PostMapping("delete")
    @ApiOperation("删除页面权限")
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(rolePageService.removeById(id));
    }

    @PostMapping("query_page_id/by_role_id")
    @ApiOperation("根据角色id，查询拥有页面id")
    public Result<List<Long>> queryPageIdByRoleId(@RequestParam Long roleId){
        List<Long> pageIds = getPageIdsByRoleId(roleId);
        return Result.success(pageIds);

    }

    private List<Long> getPageIdsByRoleId(@RequestParam Long roleId) {
        QueryWrapper<RolePage> rolePageQueryWrapper = new QueryWrapper<>();
        rolePageQueryWrapper.eq("role_id",roleId);
        List<RolePage> list = rolePageService.list(rolePageQueryWrapper);
        return list.stream().map(RolePage::getPageId).collect(Collectors.toList());
    }

    @PostMapping("query_page/by_role_id")
    @ApiOperation("根据角色id，查询拥有页面详情列表")
    public Result<List<Page>> queryPageByRoleId(@RequestParam Long roleId){
        List<Long> pageIds = getPageIdsByRoleId(roleId);
        List<Page> pages = pageService.listByIds(pageIds);
        return Result.success(pages);
    }


}

