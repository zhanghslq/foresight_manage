package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.entity.ResumeRelationship;
import com.zhs.backmanageb.service.ResumeRelationshipService;
import com.zhs.backmanageb.service.ResumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 简历关系表 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-08-12
 */
@Api(tags = "简历关系表")
@RestController
@RequestMapping("/resumeRelationship")
public class ResumeRelationshipController {
    @Resource
    private ResumeService resumeService;

    @Resource
    private ResumeRelationshipService resumeRelationshipService;


    @PostMapping("add/relation_ship")
    @ApiOperation(value = "添加特殊关联",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> addRelationShip(ResumeRelationship resumeRelationship){
        resumeRelationshipService.save(resumeRelationship);

        return Result.success(true);
    }

    @PostMapping("delete/relation_ship")
    @ApiOperation(value = "删除特殊关联",tags = "删除")
    @ApiImplicitParam(name = "relationShipId",value = "关系id")
    public Result<Boolean> deleteRelationShip(@RequestParam Long relationShipId){
        resumeRelationshipService.removeById(relationShipId);
        return Result.success(true);
    }

    @PostMapping("list/by_source_resume_id")
    @ApiOperation(value = "根据简历id，获取特殊关联的简历",tags = "查询")
    @ApiImplicitParam(name = "sourceResumeId",value = "简历id")
    public Result<List<Resume>> listBySourceResumeId(@RequestParam Long sourceResumeId){
        QueryWrapper<ResumeRelationship> resumeRelationshipQueryWrapper = new QueryWrapper<>();
        resumeRelationshipQueryWrapper.eq("source_resume_id",sourceResumeId);
        List<ResumeRelationship> list = resumeRelationshipService.list(resumeRelationshipQueryWrapper);
        List<Resume> resumes = new ArrayList<>();
        if(list.size()==0){
            return Result.success(resumes);
        }
        List<Long> resumeIds = list.stream().map(ResumeRelationship::getTargetResumeId).collect(Collectors.toList());
        resumes = resumeService.listByIds(resumeIds);
        return Result.success(resumes);

    }

}

