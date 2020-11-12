package com.zhs.controller.b.back;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.*;
import com.zhs.model.dto.ResumeDTO;
import com.zhs.model.dto.ResumeDetailDTO;
import com.zhs.model.vo.InputStatisticsVO;
import com.zhs.model.vo.ResumeVO;
import com.zhs.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.jsf.FacesContextUtils;

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
@RequestMapping("/resume")
public class ResumeController {

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
    public Result<Page<ResumeVO>> searchList(Resume resume, @RequestParam Integer current, @RequestParam Integer size, @DateTimeFormat(pattern = "yyyy-MM-dd")Date createTimeBegin,@DateTimeFormat(pattern = "yyyy-MM-dd") Date createTimeEnd){
        Page<Resume> resumePage = new Page<>(current, size);

        Page<ResumeVO> pageSelf =resumeService.pageSelf(resume,resumePage,createTimeBegin,createTimeEnd);

        return Result.success(pageSelf);
    }
    @ApiOperation(value = "插入简历",tags = "新增")
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
        // 前台添加的默认1以确认
        resume.setIsConfirm(1);
        boolean save = resumeService.save(resume);
        List<ExperienceRecord> experienceRecordList = resumeDTO.getExperienceRecordList();
        if(!Objects.isNull(experienceRecordList)&&experienceRecordList.size()>0){
            for (ExperienceRecord experienceRecord : experienceRecordList) {
                experienceRecord.setResumeId(resume.getId());
            }
            experienceRecordService.saveBatch(experienceRecordList);
        }
        List<ResumeCompany> resumeCompanyList = resumeDTO.getResumeCompanyList();
        if(!Objects.isNull(resumeCompanyList)&&resumeCompanyList.size()>0){
            for (ResumeCompany resumeCompany : resumeCompanyList) {
                resumeCompany.setResumeId(resume.getId());
                resumeCompany.setIsPolitics(0);
            }
            resumeCompanyService.saveBatch(resumeCompanyList);
        }
        List<ResumeCompany> politicsResumeCompanyList = resumeDTO.getPoliticsResumeCompanyList();

        if(!Objects.isNull(politicsResumeCompanyList)&&politicsResumeCompanyList.size()>0){
            for (ResumeCompany resumeCompany : politicsResumeCompanyList) {
                resumeCompany.setResumeId(resume.getId());
                resumeCompany.setIsPolitics(1);
            }
            resumeCompanyService.saveBatch(politicsResumeCompanyList);
        }

        return Result.success(save);
    }

    @PostMapping("update")
    @ApiOperation(value = "修改简历",tags = "修改")
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
        List<ResumeCompany> resumeCompanyList = resumeDTO.getResumeCompanyList();
        if(!Objects.isNull(resumeCompanyList)&&resumeCompanyList.size()>0){
            for (ResumeCompany resumeCompany : resumeCompanyList) {
                resumeCompany.setResumeId(resume.getId());
                resumeCompany.setIsPolitics(0);
            }
            resumeCompanyService.saveOrUpdateBatch(resumeCompanyList);
        }

        List<ResumeCompany> politicsResumeCompanyList = resumeDTO.getPoliticsResumeCompanyList();
        if(!Objects.isNull(politicsResumeCompanyList)&&politicsResumeCompanyList.size()>0){
            for (ResumeCompany resumeCompany : politicsResumeCompanyList) {
                resumeCompany.setResumeId(resume.getId());
                resumeCompany.setIsPolitics(1);
            }
            resumeCompanyService.saveOrUpdateBatch(politicsResumeCompanyList);
        }

        return Result.success(save);
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

    @ApiOperation(value = "批量删除简历",tags = "删除")
    @PostMapping("delete/by_ids")
    @ApiImplicitParam(name = "ids",value = "多个逗号相隔",required = true)
    public Result<Boolean> deleteByIds(@RequestParam List<Long> ids){
        return Result.success(resumeService.removeByIds(ids));
    }


    @ApiOperation(value = "简历文件获取简历内容",tags = "查询")
    @PostMapping("get/text")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "filename",value = "上传简历文件得到的文件名",required = true),
            @ApiImplicitParam(name = "currentStatusId",value = "状态id，来源与系统配置",required = true)
    })
    public Result<ResumeDTO> getText(@RequestParam String filename,@RequestParam Long currentStatusId){
        ResumeDTO resumeDTO = resumeService.dealWord(filename,currentStatusId,false, false);
        return Result.success(resumeDTO);

    }
    @ApiOperation(value = "批量分析简历并且入库",tags = "查询")
    @PostMapping("batch/insert_by_directory")
    public Result<Boolean> dealDirectoryResume(@RequestParam String directory){
        resumeService.dealDirectoryResume(directory);
        return Result.success(true);
    }


    @ApiOperation(value = "简历列表分类统计",tags = "查询")
    @PostMapping("resume_input/statistics")
    public Result<List<InputStatisticsVO>> expertInputStatistics(){
        List<InputStatisticsVO> inputStatisticsVO = resumeService.expertInputStatistics();
        return Result.success(inputStatisticsVO);
    }
    @PostMapping("confirm")
    @ApiOperation(value = "确认简历",tags = "修改")
    public Result<Boolean> confirm(@RequestParam Long resumeId){
        Resume resume = new Resume();
        resume.setId(resumeId);
        resume.setIsConfirm(1);
        return Result.success(resumeService.updateById(resume));
    }

}

