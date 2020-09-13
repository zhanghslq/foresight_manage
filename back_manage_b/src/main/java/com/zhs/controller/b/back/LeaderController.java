package com.zhs.controller.b.back;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Leader;
import com.zhs.service.LeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 领导人 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "领导管理")
@RestController
@RequestMapping("/leader")
public class LeaderController {
    @Resource
    private LeaderService leaderService;

    @PostMapping("insert")
    @ApiOperation(value = "插入领导",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(Leader leader){
        return Result.success(leaderService.save(leader));
    }

    @PostMapping("update")
    @ApiOperation(value = "修改领导",tags = "修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(Leader leader){
        return Result.success(leaderService.updateById(leader));
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除领导",tags = "删除")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<Boolean> deleteById(@RequestParam Long id){
        return Result.success(leaderService.removeById(id));
    }

    @PostMapping("queryById")
    @ApiOperation(value = "查询领导详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<Leader> queryById(Long id){
        return Result.success(leaderService.getById(id));
    }
    @PostMapping("organizationId")
    @ApiOperation(value = "根据组织id查询领导",tags = "查询")
    public Result<List<Leader>> listByOrganizationId(@RequestParam Long organizationId){
        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("organization_id",organizationId);
        List<Leader> list = leaderService.list(leaderQueryWrapper);
        return Result.success(list);
    }
    @PostMapping("upload/list")
    @ApiOperation(value = "导入领导人",tags = "新增")
    public Result<Boolean> uploadList(@RequestParam Long moduleId,@RequestParam("file") MultipartFile file){

        leaderService.listUpload(moduleId,file);
        return Result.success(true);
    }



}

