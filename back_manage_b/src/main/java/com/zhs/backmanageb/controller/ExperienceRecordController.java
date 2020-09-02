package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.service.ExperienceRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 履历表 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-21
 */
@Api(tags = "工作记录管理")
@RestController
@RequestMapping("/experienceRecord")
public class ExperienceRecordController {

    @Resource
    private ExperienceRecordService experienceRecordService;

    @ApiOperation(value = "插入工作记录",tags = "新增")
    @PostMapping("insert")
    public Result<Boolean> insert(@RequestBody ExperienceRecord experienceRecord){
        experienceRecordService.save(experienceRecord);
        return Result.success(true);
    }

    @PostMapping("insertBatch")
    @ApiOperation(value = "批量插入工作记录",tags = "新增")
    public Result<Boolean> insert(@RequestBody List<ExperienceRecord> experienceRecords){
        return Result.success(experienceRecordService.saveBatch(experienceRecords));
    }
    @PostMapping("delete")
    @ApiOperation(value = "删除工作记录",tags = "新增")
    public Result<Boolean> delete(@RequestParam Long id){
        experienceRecordService.removeById(id);
        return Result.success(true);
    }
    @PostMapping("update")
    @ApiOperation(value = "根据id修改工作记录",tags = "修改")
    public Result<Boolean> update(@RequestBody ExperienceRecord experienceRecord){
        experienceRecordService.updateById(experienceRecord);
        return Result.success(true);
    }

    @PostMapping("updateBatch")
    @ApiOperation(value = "批量根据id修改工作记录",tags = "修改")
    public Result<Boolean> updateBatch(@RequestBody List<ExperienceRecord> experienceRecords){
        experienceRecordService.updateBatchById(experienceRecords);
        return Result.success(true);
    }

    @PostMapping("saveOrUpdateBatch")
    @ApiOperation(value = "批量根据idx新增或修改工作记录",tags = "修改")
    public Result<Boolean> insertOrUpdateBatch(@RequestBody List<ExperienceRecord> experienceRecords){
        experienceRecordService.saveOrUpdateBatch(experienceRecords);
        return Result.success(true);
    }

    @PostMapping("list/by_resume_id")
    @ApiOperation(value = "根据简历id查询工作经历",tags = "查询")
    public Result<List<ExperienceRecord>> listByResumeId(@RequestParam Long resumeId){
        QueryWrapper<ExperienceRecord> experienceRecordQueryWrapper = new QueryWrapper<>();
        experienceRecordQueryWrapper.eq("resume_id",resumeId);
        return Result.success(experienceRecordService.list(experienceRecordQueryWrapper));
    }
    @ApiOperation(value = "批量删除工作记录",tags = "删除")
    @PostMapping("delete/by_ids")
    @ApiImplicitParam(name = "ids",value = "多个逗号相隔",required = true)
    public Result<Boolean> deleteByIds(@RequestParam List<Long> ids){
        return Result.success(experienceRecordService.removeByIds(ids));
    }

}

