package com.zhs.controller.b.back;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Page;
import com.zhs.entity.RolePage;
import com.zhs.service.PageService;
import com.zhs.service.RolePageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @ApiOperation(value = "增加角色页面权限",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(RolePage rolePage){
        return Result.success(rolePageService.save(rolePage));
    }
    @PostMapping("insertBatch")
    @ApiOperation(value = "批量添加角色页面权限",tags = "新增")
    public Result<Boolean> insertBatch(@RequestBody List<RolePage>rolePages){
        return Result.success(rolePageService.saveBatch(rolePages));
    }

    @PostMapping("update_page/by_tole_id")
    @ApiOperation(value = "更新角色权限",tags = "修改")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId",value = "角色id",required = true),
            @ApiImplicitParam(name = "pageIds",value = "页面权限id，多个逗号相隔",required = true),
    })
    public Result<Boolean> updatePageByRoleId(@RequestParam Long roleId,@RequestParam List<Long> pageIds){
        QueryWrapper<RolePage> rolePageQueryWrapper = new QueryWrapper<>();
        rolePageQueryWrapper.eq("role_id",roleId);
        List<RolePage> list = rolePageService.list(rolePageQueryWrapper);
        List<Long> pageIdList = list.stream().map(RolePage::getPageId).collect(Collectors.toList());
        List<Long> pageIdList2 = new ArrayList<>(pageIdList);
        // 剩下是需要删除的,差集
        pageIdList.removeAll(pageIds);
        List<Long> removeList = list.stream().filter(rolePage -> pageIdList.contains(rolePage.getPageId())).map(RolePage::getId).collect(Collectors.toList());
        if(removeList.size()>0){
            rolePageService.removeByIds(removeList);
        }
        // 并集，都是不需要插入的
        pageIdList2.retainAll(pageIds);
        ArrayList<RolePage> insertList = new ArrayList<>();
        for (Long pageId : pageIds) {
            if(!pageIdList2.contains(pageId)){
                // 原本不存在的新加
                RolePage rolePage = new RolePage();
                rolePage.setRoleId(roleId);
                rolePage.setPageId(pageId);
                insertList.add(rolePage);
            }
        }
        if(insertList.size()>0){
            rolePageService.saveBatch(insertList);
        }
        return Result.success(true);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除页面权限",tags = "删除")
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(rolePageService.removeById(id));
    }

    @PostMapping("query_page_id/by_role_id")
    @ApiOperation(value = "根据角色id，查询拥有页面id",tags = "查询")
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
    @ApiOperation(value = "根据角色id，查询拥有页面详情列表",tags = "查询")
    public Result<List<Page>> queryPageByRoleId(@RequestParam Long roleId){
        List<Long> pageIds = getPageIdsByRoleId(roleId);
        List<Page> pages = pageService.listByIds(pageIds);
        return Result.success(pages);
    }


}

