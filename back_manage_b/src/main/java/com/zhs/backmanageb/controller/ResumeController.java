package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.model.dto.ResumeDTO;
import com.zhs.backmanageb.model.vo.ResumeVO;
import com.zhs.backmanageb.service.ExperienceRecordService;
import com.zhs.backmanageb.service.ResumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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

    @ApiOperation(value = "简历列表",tags = "查询")
    @PostMapping("list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    public Result<Page<ResumeVO>> list(@RequestParam Integer current,@RequestParam Integer size){
        Page<Resume> resumePage = new Page<>(current, size);

        Page<ResumeVO> pageSelf =resumeService.pageSelf(resumePage);

        return Result.success(pageSelf);
    }
    @ApiOperation(value = "插入",tags = "新增")
    @PostMapping("insert")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(@RequestBody ResumeDTO resumeDTO){
        Resume resume = resumeDTO.getResume();
        // 插入的时候需要记录操作人id
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            Long adminId = Long.valueOf(principal.toString());
            resume.setAdminId(adminId);
        } catch (NumberFormatException e) {
            resume.setAdminId(0L);
        }
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
    @ApiOperation(value = "修改",tags = "修改")
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
    @ApiOperation(value = "查询详情",tags = "查询")
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

    @ApiOperation(value = "批量删除",tags = "删除")
    @PostMapping("delete/by_ids")
    @ApiImplicitParam(name = "ids",value = "多个逗号相隔",required = true)
    public Result<Boolean> deleteByIds(@RequestParam List<Long> ids){
        return Result.success(resumeService.removeByIds(ids));
    }

}

