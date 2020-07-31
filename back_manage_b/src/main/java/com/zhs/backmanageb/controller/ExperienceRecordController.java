package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.service.ExperienceRecordService;
import io.swagger.annotations.Api;
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

    @ApiOperation("插入工作记录")
    @PostMapping("insert")
    public Result<Boolean> insert(@RequestBody ExperienceRecord experienceRecord){
        experienceRecordService.save(experienceRecord);
        return Result.success(true);
    }

    @PostMapping("insertBatch")
    @ApiOperation("批量插入")
    public Result<Boolean> insert(@RequestBody List<ExperienceRecord> experienceRecords){
        return Result.success(experienceRecordService.saveBatch(experienceRecords));
    }
    @PostMapping("delete")
    @ApiOperation("删除")
    public Result<Boolean> delete(@RequestParam Long id){
        experienceRecordService.removeById(id);
        return Result.success(true);
    }
    @PostMapping("update")
    @ApiOperation("根据id修改")
    public Result<Boolean> update(@RequestBody ExperienceRecord experienceRecord){
        experienceRecordService.updateById(experienceRecord);
        return Result.success(true);
    }

    @PostMapping("updateBatch")
    @ApiOperation("批量根据id修改")
    public Result<Boolean> updateBatch(@RequestBody List<ExperienceRecord> experienceRecords){
        experienceRecordService.updateBatchById(experienceRecords);
        return Result.success(true);
    }

    @PostMapping("list/by_resume_id")
    @ApiOperation("根据简历id查询工作经历")
    public Result<List<ExperienceRecord>> listByResumeId(@RequestParam Long resumeId){
        QueryWrapper<ExperienceRecord> experienceRecordQueryWrapper = new QueryWrapper<>();
        experienceRecordQueryWrapper.eq("resume_id",resumeId);
        return Result.success(experienceRecordService.list(experienceRecordQueryWrapper));
    }


}

