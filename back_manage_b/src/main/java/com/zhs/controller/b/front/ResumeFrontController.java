package com.zhs.controller.b.front;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.DownBoxData;
import com.zhs.entity.ExperienceRecord;
import com.zhs.entity.Resume;
import com.zhs.entity.ResumeCompany;
import com.zhs.model.dto.ResumeDTO;
import com.zhs.model.dto.ResumeDetailDTO;
import com.zhs.model.vo.InputStatisticsVO;
import com.zhs.model.vo.ResumeVO;
import com.zhs.service.DownBoxDataService;
import com.zhs.service.ExperienceRecordService;
import com.zhs.service.ResumeCompanyService;
import com.zhs.service.ResumeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
@RequestMapping("/resume_front")
public class ResumeFrontController {

    @Resource
    private ResumeService resumeService;
    @Resource
    private ExperienceRecordService experienceRecordService;

    @Autowired
    private ResumeCompanyService resumeCompanyService;

    @Autowired
    private DownBoxDataService downBoxDataService;

    @ApiOperation(value = "按照条件查询简历列表",tags = "查询")
    @PostMapping("search/list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    @ApiOperationSupport(ignoreParameters = {"deleted"})
    public Result<Page<ResumeVO>> searchList(Resume resume, @RequestParam Integer current, @RequestParam Integer size,Date createTimeBegin,Date createTimeEnd){
        Page<Resume> resumePage = new Page<>(current, size);

        Page<ResumeVO> pageSelf =resumeService.pageSelf(resume,resumePage, createTimeBegin, createTimeEnd);

        return Result.success(pageSelf);
    }

    @PostMapping("queryById")
    @ApiOperation(value = "查询简历详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<ResumeDTO> queryById(@RequestParam Long id){
        Resume resume = resumeService.getById(id);
        QueryWrapper<ExperienceRecord> experienceRecordQueryWrapper = new QueryWrapper<>();
        experienceRecordQueryWrapper.eq("resume_id",id);
        List<ExperienceRecord> experienceRecordList = experienceRecordService.list(experienceRecordQueryWrapper);

        QueryWrapper<ResumeCompany> resumeCompanyQueryWrapper = new QueryWrapper<>();
        resumeCompanyQueryWrapper.eq("resume_id",id);
        resumeCompanyQueryWrapper.eq("is_politics",0);
        List<ResumeCompany> resumeCompanyList = resumeCompanyService.list(resumeCompanyQueryWrapper);
        ResumeDTO resumeDTO = new ResumeDTO();
        resumeDTO.setResume(resume);
        resumeDTO.setExperienceRecordList(experienceRecordList);
        resumeDTO.setResumeCompanyList(resumeCompanyList);

        QueryWrapper<ResumeCompany> politicsResumeCompanyQueryWrapper = new QueryWrapper<>();
        politicsResumeCompanyQueryWrapper.eq("resume_id",id);
        politicsResumeCompanyQueryWrapper.eq("is_politics",1);
        List<ResumeCompany> politicsResumeCompanyList = resumeCompanyService.list(politicsResumeCompanyQueryWrapper);
        resumeDTO.setPoliticsResumeCompanyList(politicsResumeCompanyList);
        return Result.success(resumeDTO);
    }
    @PostMapping("query_detail/by_Id")
    @ApiOperation(value = "查询简历详细信息",tags = "查询")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<ResumeDetailDTO> queryDetailById(@RequestParam Long id){
        Resume resume = resumeService.getById(id);
        QueryWrapper<ExperienceRecord> experienceRecordQueryWrapper = new QueryWrapper<>();
        experienceRecordQueryWrapper.eq("resume_id",id);
        List<ExperienceRecord> experienceRecordList = experienceRecordService.list(experienceRecordQueryWrapper);
        ResumeDetailDTO resumeDetailDTO = new ResumeDetailDTO();
        ResumeVO resumeVO = new ResumeVO();

        List<ExperienceRecord>  lastExperienceList = experienceRecordService.queryLastExperience(Collections.singletonList(id));
        // 工作时间
        Map<Long, Date> workMap = lastExperienceList.stream().collect(Collectors.toMap(ExperienceRecord::getResumeId, ExperienceRecord::getBeginDate));
        BeanUtil.copyProperties(resume,resumeVO);
        // 然后把字段值填上
        // 行政级别
        if(!Objects.isNull(resumeVO.getLevelId())){
            DownBoxData byId = downBoxDataService.getById(resume.getLevelId());
            if(!Objects.isNull(byId)){
                resumeVO.setLevelName(byId.getName());
            }
        }
        Date birthday = resume.getBirthday();
        if(!Objects.isNull(birthday)){
            int age = DateUtil.ageOfNow(birthday);
            resumeVO.setAge(age);
        }
        // 获取最后一段经历计算在职时间
        Date date = workMap.get(resume.getId());
        if(!Objects.isNull(date)){
            long day = DateUtil.between(date, new Date(), DateUnit.DAY);
            resumeVO.setWorkingDays(day+"天");
            resumeVO.setBeginWorkingTime(date);
        }
        if(Objects.isNull(resume.getCurrentStatus())&&!Objects.isNull(resume.getCurrentStatusId())){
            DownBoxData byId = downBoxDataService.getById(resume.getCurrentStatusId());
            if(!Objects.isNull(byId)){
                resumeVO.setCurrentStatus(byId.getName());
            }
        }
        QueryWrapper<ResumeCompany> resumeCompanyQueryWrapper = new QueryWrapper<>();
        resumeCompanyQueryWrapper.eq("resume_id",id);
        resumeCompanyQueryWrapper.eq("is_politics",0);
        List<ResumeCompany> resumeCompanyList = resumeCompanyService.list(resumeCompanyQueryWrapper);
        resumeVO.setResumeCompanyList(resumeCompanyList);

        if(!Objects.isNull(resumeVO.getParties())){
            DownBoxData commonData = downBoxDataService.getById(resumeVO.getParties());
            String name = commonData.getName();
            resumeVO.setPartiesName(name);
        }
        if(!Objects.isNull(resumeVO.getNation())){
            DownBoxData commonData = downBoxDataService.getById(resumeVO.getNation());
            String name = commonData.getName();
            resumeVO.setNationName(name);
        }
        QueryWrapper<ResumeCompany> politicsResumeCompanyQueryWrapper = new QueryWrapper<>();
        politicsResumeCompanyQueryWrapper.eq("resume_id",id);
        politicsResumeCompanyQueryWrapper.eq("is_politics",1);
        List<ResumeCompany> politicsResumeCompanyList = resumeCompanyService.list(politicsResumeCompanyQueryWrapper);
        resumeVO.setPoliticsResumeCompanyList(politicsResumeCompanyList);

        resumeDetailDTO.setResumeVO(resumeVO);

        resumeDetailDTO.setExperienceRecordList(experienceRecordList);
        return Result.success(resumeDetailDTO);
    }
    @ApiOperation(value = "简历列表分类统计",tags = "查询")
    @PostMapping("resume_input/statistics")
    public Result<List<InputStatisticsVO>> expertInputStatistics(){
        List<InputStatisticsVO> inputStatisticsVO = resumeService.expertInputStatistics();
        return Result.success(inputStatisticsVO);
    }
    @PostMapping("list/gender_and_level")
    @ApiOperation(value = "人事级别性别对比")
    public Result<Object> genderRate(){

        return Result.success("");
    }
    @PostMapping("list/personnel_changes")
    @ApiOperation(value = "人事变化",tags = "查询")
    public Result<Object> listPersonalChange(@RequestParam Date startDate){
        // 最近多久的数据

        return Result.success("");
    }
}

