package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.service.ResumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "简历管理")
@RestController
@RequestMapping("/resume")
public class ResumeController {

    @Resource
    private ResumeService resumeService;

    @ApiOperation("简历列表")
    @PostMapping("list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    public Result<Page<Resume>> list(@RequestParam Integer current,@RequestParam Integer size){
        Page<Resume> resumePage = new Page<>(current, size);
        return Result.success(resumeService.page(resumePage));
    }
    @ApiOperation("插入")
    @PostMapping("insert")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(@RequestBody Resume resume){
        boolean save = resumeService.save(resume);
        return Result.success(save);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(@RequestBody Resume resume) {
        return Result.success(resumeService.updateById(resume));
    }
    @PostMapping("queryById")
    @ApiOperation("查询详情")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<Resume> queryById(@RequestParam Long id){
        return Result.success(resumeService.getById(id));
    }


}

