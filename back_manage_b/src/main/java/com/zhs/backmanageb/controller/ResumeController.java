package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.model.dto.ResumeDTO;
import com.zhs.backmanageb.service.ExperienceRecordService;
import com.zhs.backmanageb.service.ResumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
    @Resource
    private ExperienceRecordService experienceRecordService;

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
    public Result<Boolean> insert(@RequestBody ResumeDTO resumeDTO){
        Resume resume = resumeDTO.getResume();
        boolean save = resumeService.save(resume);
        List<ExperienceRecord> experienceRecordList = resumeDTO.getExperienceRecordList();
        if(!Objects.isNull(experienceRecordList)&&experienceRecordList.size()>0){
            for (ExperienceRecord experienceRecord : experienceRecordList) {
                experienceRecord.setResumeId(resume.getId());
            }
            experienceRecordService.saveBatch(experienceRecordList);
        }
        return Result.success(save);
    }

    @PostMapping("update")
    @ApiOperation("修改")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(@RequestBody ResumeDTO resumeDTO) {
        Resume resume = resumeDTO.getResume();
        boolean save = resumeService.updateById(resume);
        List<ExperienceRecord> experienceRecordList = resumeDTO.getExperienceRecordList();
        if(!Objects.isNull(experienceRecordList)&&experienceRecordList.size()>0){
            for (ExperienceRecord experienceRecord : experienceRecordList) {
                experienceRecord.setResumeId(resume.getId());
            }
            experienceRecordService.saveOrUpdateBatch(experienceRecordList);
        }
        return Result.success(save);
    }
    @PostMapping("queryById")
    @ApiOperation("查询详情")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<ResumeDTO> queryById(@RequestParam Long id){
        Resume resume = resumeService.getById(id);
        QueryWrapper<ExperienceRecord> experienceRecordQueryWrapper = new QueryWrapper<>();
        experienceRecordQueryWrapper.eq("resume_id",id);
        List<ExperienceRecord> experienceRecordList = experienceRecordService.list(experienceRecordQueryWrapper);
        ResumeDTO resumeDTO = new ResumeDTO();
        resumeDTO.setResume(resume);
        resumeDTO.setExperienceRecordList(experienceRecordList);
        return Result.success(resumeDTO);
    }


}

