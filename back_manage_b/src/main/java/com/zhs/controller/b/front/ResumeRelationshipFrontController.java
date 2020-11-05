package com.zhs.controller.b.front;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Resume;
import com.zhs.entity.ResumeRelationship;
import com.zhs.model.vo.ResumeRelationshipVO;
import com.zhs.service.ResumeRelationshipService;
import com.zhs.service.ResumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
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
@RequestMapping("/resumeRelationship_front")
public class ResumeRelationshipFrontController {
    @Resource
    private ResumeService resumeService;

    @Resource
    private ResumeRelationshipService resumeRelationshipService;



    @PostMapping("list/by_source_resume_id")
    @ApiOperation(value = "根据简历id，获取特殊关联的简历",tags = "查询")
    @ApiImplicitParam(name = "sourceResumeId",value = "简历id")
    public Result<List<ResumeRelationshipVO>> listBySourceResumeId(@RequestParam Long sourceResumeId){
        QueryWrapper<ResumeRelationship> resumeRelationshipQueryWrapper = new QueryWrapper<>();

        resumeRelationshipQueryWrapper.eq("source_resume_id",sourceResumeId);
        List<ResumeRelationship> list = resumeRelationshipService.list(resumeRelationshipQueryWrapper);
        List<ResumeRelationshipVO> result = new ArrayList<>();
        if(list.size()==0){
            return Result.success(result);
        }
        List<Long> resumeIds = list.stream().map(ResumeRelationship::getTargetResumeId).collect(Collectors.toList());
        List<Resume> resumes = resumeService.listByIds(resumeIds);

        // 关系id对应的简历
        Map<Long, ResumeRelationshipVO> relationshipVOHashMap = new HashMap<>();
        Map<Long, ResumeRelationship> map = list.stream().collect(Collectors.toMap(ResumeRelationship::getTargetResumeId, resumeRelationship -> resumeRelationship, (k1, k2) -> k1));
        for (Resume resume : resumes) {

            ResumeRelationship resumeRelationship = map.get(resume.getId());
            if(Objects.nonNull(resumeRelationship)){
                //不为空的时候
                Long relationshipId = resumeRelationship.getId();
                ResumeRelationshipVO resumeRelationshipVO = relationshipVOHashMap.get(relationshipId);
                if(Objects.isNull(resumeRelationshipVO)){
                    resumeRelationshipVO = new ResumeRelationshipVO();
                    resumeRelationshipVO.setRelationship(resumeRelationship.getRelationship());
                    resumeRelationshipVO.setRelationshipId(resumeRelationship.getRelationshipId());
                    resumeRelationshipVO.setId(resumeRelationship.getId());
                    relationshipVOHashMap.put(relationshipId,resumeRelationshipVO);
                }
                List<Resume> resumeList = resumeRelationshipVO.getResumeList();
                if(Objects.isNull(resumeList)){
                    resumeList = new ArrayList<>();
                    resumeRelationshipVO.setResumeList(resumeList);
                }
                if(Objects.isNull(resumeRelationshipVO.getRelationship())){
                    resumeRelationshipVO.setRelationship(resumeRelationship.getRelationship());
                    resumeRelationshipVO.setRelationshipId(resumeRelationship.getRelationshipId());
                    resumeRelationshipVO.setId(resumeRelationship.getId());
                }
                resumeList.add(resume);
            }
        }
        result = new ArrayList<>(relationshipVOHashMap.values());
        return Result.success(result);

    }

}

