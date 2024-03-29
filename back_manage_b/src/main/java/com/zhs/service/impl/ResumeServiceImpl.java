package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.ScopeEnum;
import com.zhs.entity.*;
import com.zhs.exception.MyException;
import com.zhs.mapper.ResumeMapper;
import com.zhs.model.bo.*;
import com.zhs.model.dto.ExpierenceRecordConvertDTO;
import com.zhs.model.dto.ResumeConvertDTO;
import com.zhs.model.dto.ResumeDTO;
import com.zhs.model.vo.*;
import com.zhs.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.util.AsposeWordUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Slf4j
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    @Autowired
    private AreaService areaService;

    @Autowired
    private ExperienceRecordService experienceRecordService;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private ResumeCompanyService resumeCompanyService;

    @Autowired
    private DownBoxDataService downBoxDataService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @Override
    public Page<ResumeVO> pageSelf(Resume resume, Page<Resume> resumePage, Date createTimeBegin, Date createTimeEnd) {

        List<DownBoxData> list = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_LEVEL.getId(), ScopeEnum.RESUME.getId());


        Map<Integer, String> map = list.stream().collect(Collectors.toMap(DownBoxData::getId, DownBoxData::getName));


        List<DownBoxData> statusList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.RESUME_STATUS.getId(), ScopeEnum.RESUME.getId());

        Map<Integer, String> statusMap = statusList.stream().collect(Collectors.toMap(DownBoxData::getId, DownBoxData::getName));

        List<ResumeVO> resumeVOS = new ArrayList<>();
        Page<ResumeVO> resumeVOPage = new Page<>();
        Page<Resume> page;
        if(Objects.isNull(resume)){
            page = page(resumePage);
        }else {
            QueryWrapper<Resume> resumeQueryWrapper = new QueryWrapper<>();
            // 简历编号
            if(!Objects.isNull(resume.getId())){
                resumeQueryWrapper.eq("id",resume.getId());
            }
            if(Objects.nonNull(createTimeBegin)&&Objects.nonNull(createTimeEnd)){
                resumeQueryWrapper.ge("create_time",createTimeBegin);
                resumeQueryWrapper.le("create_time",createTimeEnd);
            }
            //姓名
            resumeQueryWrapper.like(!StringUtils.isEmpty(resume.getRealName()),"real_name",resume.getRealName());
            //创建人
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getAdminId()),"admin_id",resume.getAdminId());
            // 行政级别
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getLevelId()),"level_id",resume.getLevelId());
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getLevelName()),"level_name",resume.getLevelName());
            //现任单位
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getCompany()),"company",resume.getCompany());
            //现任职位
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getJob()),"job",resume.getJob());
            //现任职时间

            // 这俩跟别的不算同一个维度的，
            //在职时间

            //出生地
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getAreaId()),"area_id",resume.getAreaId());
            resumeQueryWrapper.like(!StringUtils.isEmpty(resume.getAreaName()),"area_name",resume.getAreaName());
            //目前状态
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getCurrentStatus()),"current_status",resume.getCurrentStatus());
            resumeQueryWrapper.eq(!StringUtils.isEmpty(resume.getCurrentStatusId()),"current_status_id",resume.getCurrentStatusId());
            page = page(resumePage,resumeQueryWrapper);
        }
        // 对page进行处理
        List<Resume> records = page.getRecords();
        if(records.size()==0){
            return resumeVOPage;
        }
        List<Long> resumeIds = records.stream().map(Resume::getId).collect(Collectors.toList());
        List<Long> adminIds = records.stream().map(Resume::getAdminId).collect(Collectors.toList());
        List<Admin> adminList = adminService.listByIds(adminIds);
        Map<Long, Admin> adminMap = adminList.stream().collect(Collectors.toMap(Admin::getId, admin -> admin));
        List<ExperienceRecord>  lastExperienceList = experienceRecordService.queryLastExperience(resumeIds);
        // 工作时间
        Map<Long, Date> workMap = lastExperienceList.stream().collect(Collectors.toMap(ExperienceRecord::getResumeId, ExperienceRecord::getBeginDate));
        BeanUtil.copyProperties(page,resumeVOPage);

        // 查一下对应的单位职务
        QueryWrapper<ResumeCompany> resumeCompanyQueryWrapper = new QueryWrapper<>();
        resumeCompanyQueryWrapper.in("resume_id",resumeIds);
        resumeCompanyQueryWrapper.eq("is_politics",0);
        List<ResumeCompany> resumeCompanyList = resumeCompanyService.list(resumeCompanyQueryWrapper);
        Map<Long, List<ResumeCompany>> resumeCompanyMap = resumeCompanyList.stream().collect(Collectors.groupingBy(ResumeCompany::getResumeId));


        QueryWrapper<ResumeCompany> politicsResumeCompanyQueryWrapper = new QueryWrapper<>();
        politicsResumeCompanyQueryWrapper.in("resume_id",resumeIds);
        politicsResumeCompanyQueryWrapper.eq("is_politics",1);
        List<ResumeCompany> politicsResumeCompanyList = resumeCompanyService.list(politicsResumeCompanyQueryWrapper);
        Map<Long, List<ResumeCompany>> politicsResumeCompanyMap = politicsResumeCompanyList.stream().collect(Collectors.groupingBy(ResumeCompany::getResumeId));


        for (Resume record : records) {
            ResumeVO resumeVO = new ResumeVO();
            BeanUtil.copyProperties(record,resumeVO);
            // 然后把字段值填上
            resumeVO.setResumeCompanyList(resumeCompanyMap.get(record.getId()));
            resumeVO.setPoliticsResumeCompanyList(politicsResumeCompanyMap.get(record.getId()));
            // 行政级别
            if(Objects.nonNull(resumeVO.getLevelId())){
                resumeVO.setLevelName(map.get(resumeVO.getLevelId().intValue()));
            }
            Date birthday = record.getBirthday();
            if(!Objects.isNull(birthday)){
                int age =DateUtil.ageOfNow(birthday);
                resumeVO.setAge(age);
            }
            // 获取最后一段经历计算在职时间
            Date date = workMap.get(record.getId());
            if(!Objects.isNull(date)){
                long day = DateUtil.between(date, new Date(), DateUnit.DAY);
                resumeVO.setWorkingDays(day+"天");
                resumeVO.setBeginWorkingTime(date);
            }
            if(Objects.isNull(record.getCurrentStatus())&&!Objects.isNull(record.getCurrentStatusId())){
                resumeVO.setCurrentStatus(statusMap.get(record.getCurrentStatusId().intValue()));
            }
            Admin admin = adminMap.get(record.getAdminId());
            if(Objects.nonNull(admin)){
                resumeVO.setAdminName(admin.getRealName());
            }
            resumeVOS.add(resumeVO);
        }
        resumeVOPage.setRecords(resumeVOS);
        return resumeVOPage;
    }

    @Override
    public ResumeDTO dealWord(String filename, Long currentStatusId, boolean isPushData,boolean isAbsolutePath) {
        String prefix;
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            prefix = "c://data/file/";
        }else {
            prefix = "/data/file/";
        }
        Resume resume = new Resume();
        DownBoxData byId = downBoxDataService.getById(currentStatusId);
        Assert.notNull(byId,"状态不存在");
        ResumeDTO resumeDTO = new ResumeDTO();
        String text;
        if(isAbsolutePath){
            text = AsposeWordUtil.getText(filename);
        }else {
            text = AsposeWordUtil.getText(prefix+filename);
        }
        // 拿到text到php进行请求，获取结果
        try {

            List<String> imageFromWordFile;
            if(isAbsolutePath){
                imageFromWordFile = AsposeWordUtil.exportImageFromWordFile(filename, prefix);

            }else {
                imageFromWordFile = AsposeWordUtil.exportImageFromWordFile(prefix+filename, prefix);
            }
            if(imageFromWordFile.size()>0){
                resume.setPhotoUrl(imageFromWordFile.get(0));
            }
        } catch (Exception e) {
            log.error("获取图片出错,:",e);
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        //RestTemplate带参传的时候要用HttpEntity<?>对象传递
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("resumeText", text);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity("http://data.yuanjiandata.com/resume/index/index/", request, String.class);
        //获取3方接口返回的数据通过entity.getBody();它返回的是一个字符串；
        String body = entity.getBody();
        //  简历对应的单位和职务改为多个
        if(!StringUtils.isEmpty(body)){
            JSONArray jsonArray = JSONArray.parseArray(body);
            /*
             * 0=个人信息
             * 1=履历
             * 2=传入的内容
             */
            Object o = jsonArray.get(0);
            ResumeConvertDTO resumeConvertDTO = JSONObject.parseObject(o.toString(), ResumeConvertDTO.class);

            /*
             * name : 林武
             * sex : 男
             * nation : 汉族
             * parties : 中国共产党
             * level :
             * company : 山西省委、山西省政府
             * position : 军民融合办主任、副书记、省长、党组书记
             * positionDate : 2020-01
             * birthday : 1962-2
             * age : 58
             * birthplace : 福建/闽侯
             * workStatus : 在职
             * politicsCompany :
             * politicsPosition :
             * photo :
             */
            resume.setRealName(resumeConvertDTO.getName());
            String sex = resumeConvertDTO.getSex();

            if(sex.contains("男")){
                resume.setSex(1);
            }else if(sex.contains("女")){
                resume.setSex(0);
            }
            resume.setSexName(sex);
            String birthday = resumeConvertDTO.getBirthday();
            Date date = Convert.toDate(birthday);
            if(Objects.isNull(date)){
                date=Convert.toDate(birthday+"-01");
            }
            if(Objects.isNull(date)){
                date=Convert.toDate(birthday+"-06-01");
            }
            resume.setBirthday(date);
            resume.setBirthdayString(birthday);
            resume.setJob(resumeConvertDTO.getPosition());
            resume.setOrganization(resumeConvertDTO.getPoliticsCompany());
            resume.setOrganizationJob(resumeConvertDTO.getPoliticsPosition());



            resume.setCompany(resumeConvertDTO.getCompany());
            resume.setJob(resumeConvertDTO.getPosition());
            resume.setAreaName(resumeConvertDTO.getBirthplace());
            List<Integer> areaIdArray = new ArrayList<>();
            if(!StringUtils.isEmpty(resumeConvertDTO.getBirthplaceId())){
                String birthplaceId = resumeConvertDTO.getBirthplaceId();
                String[] split = birthplaceId.split(",");
                try {
                    for (String s : split) {
                        areaIdArray.add(Integer.valueOf(s));
                        resume.setAreaId(Long.valueOf(s));
                    }
                } catch (NumberFormatException e) {
                    log.error("解析出错了字符串为:{}",birthplaceId);
                }
                resume.setAreaIdArray(JSON.toJSONString(areaIdArray));
            }

            resume.setNationName(resumeConvertDTO.getNation());


            List<DownBoxData> nationList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.NATION.getId(), ScopeEnum.RESUME.getId());
            if(nationList.size()>0){
                Optional<DownBoxData> first = nationList.stream().filter(downBoxData -> downBoxData.getName().equals(resumeConvertDTO.getNation())).findFirst();
                first.ifPresent(downBoxData ->{
                    Integer nationId = downBoxData.getId();
                    resume.setNation(nationId.longValue());
                    List<Integer> nationIds = new ArrayList<>();
                    nationIds.add(nationId);
                    Map<Integer, DownBoxData> collect = nationList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                    dealArray(nationIds,collect);
                    Collections.reverse(nationIds);
                    resume.setNationIdArray(JSONArray.toJSONString(nationIds));
                });

            }
            resume.setPartiesName(resumeConvertDTO.getParties());

            List<DownBoxData> partiesList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.PARTIES.getId(), ScopeEnum.RESUME.getId());

            if(partiesList.size()>0){
                Optional<DownBoxData> first = partiesList.stream().filter(downBoxData -> downBoxData.getName().equals(resumeConvertDTO.getParties())).findFirst();
                first.ifPresent(downBoxData ->{
                    Integer partiesId = downBoxData.getId();
                    resume.setParties(partiesId.longValue());
                    List<Integer> partiesIds = new ArrayList<>();
                    partiesIds.add(partiesId);
                    Map<Integer, DownBoxData> collect = partiesList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                    dealArray(partiesIds,collect);
                    Collections.reverse(partiesIds);
                    resume.setPartiesArray(JSONArray.toJSONString(partiesIds));
                });

            }
            resume.setWordUrl(filename);
            resume.setCurrentStatusId(currentStatusId);
            resume.setCurrentStatus(byId.getName());

            List<DownBoxData> statusList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.RESUME_STATUS.getId(), ScopeEnum.RESUME.getId());
            Optional<DownBoxData> first = statusList.stream().filter(downBoxData -> downBoxData.getName().equals(resume.getCurrentStatus())).findFirst();
            first.ifPresent(downBoxData ->{
                Integer statusId = downBoxData.getId();
                List<Integer> statusIds = new ArrayList<>();
                statusIds.add(statusId);
                Map<Integer, DownBoxData> collect = statusList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                dealArray(statusIds,collect);
                Collections.reverse(statusIds);
                resume.setCurrentStatusIdArray(JSONArray.toJSONString(statusIds));
            });

            resume.setWordContent(jsonArray.get(2).toString());
            if(isPushData){
                // 自动分析入库的标志
                resume.setSource(1);
                save(resume);
            }

            String position = resumeConvertDTO.getPosition();
            // 当前职务
            List<OrganizationConvertBO> organizationConvertBOS = JSONArray.parseArray(position, OrganizationConvertBO.class);
            if(!Objects.isNull(organizationConvertBOS)){
                if(organizationConvertBOS.size()>0){
                    List<ResumeCompany> resumeCompanies = new ArrayList<>();
                    StringBuilder stringBuilderCompany = new StringBuilder();
                    StringBuilder stringBuilderJob = new StringBuilder();

                    for (OrganizationConvertBO organizationConvertBO : organizationConvertBOS) {
                        ResumeCompany resumeCompany = new ResumeCompany();
                        resumeCompany.setCompany(organizationConvertBO.getOrganization());
                        resumeCompany.setResumeId(resume.getId());
                        resumeCompany.setJob(organizationConvertBO.getPosition());
                        resumeCompany.setIsPolitics(0);
                        resumeCompanies.add(resumeCompany);

                        stringBuilderCompany.append(organizationConvertBO.getOrganization());
                        stringBuilderJob.append(organizationConvertBO.getPosition());
                    }
                    resume.setCompany(stringBuilderCompany.toString());
                    resume.setJob(stringBuilderJob.toString());
                    if(isPushData){
                         updateById(resume);
                         resumeCompanyService.saveBatch(resumeCompanies);
                    }
                    resumeDTO.setResumeCompanyList(resumeCompanies);
                }
            }
            String politics = resumeConvertDTO.getPolitics();
            // 政治身份
            List<OrganizationConvertBO> politicsList = JSONArray.parseArray(politics, OrganizationConvertBO.class);
            if(!Objects.isNull(politicsList)&&politicsList.size()>0){
                List<ResumeCompany> politicsResumeCompanies = new ArrayList<>();
                StringBuilder stringBuilderCompany = new StringBuilder();
                StringBuilder stringBuilderJob = new StringBuilder();
                for (OrganizationConvertBO organizationConvertBO : politicsList) {
                    ResumeCompany resumeCompany = new ResumeCompany();
                    resumeCompany.setCompany(organizationConvertBO.getOrganization());
                    resumeCompany.setResumeId(resume.getId());
                    resumeCompany.setJob(organizationConvertBO.getPosition());
                    resumeCompany.setIsPolitics(1);
                    politicsResumeCompanies.add(resumeCompany);

                    stringBuilderCompany.append(organizationConvertBO.getOrganization());
                    stringBuilderJob.append(organizationConvertBO.getPosition());
                }
                resume.setOrganization(stringBuilderCompany.toString());
                resume.setOrganizationJob(stringBuilderJob.toString());
                if(isPushData){
                    updateById(resume);
                    resumeCompanyService.saveBatch(politicsResumeCompanies);
                }
                resumeDTO.setPoliticsResumeCompanyList(politicsResumeCompanies);
            }


            Object o1 = jsonArray.get(1);
            if(!Objects.isNull(o1)){
                List<ExpierenceRecordConvertDTO> expierenceRecordConvertDTOS = JSONArray.parseArray(o1.toString(), ExpierenceRecordConvertDTO.class);
                ArrayList<ExperienceRecord> experienceRecords = new ArrayList<>();
                for (ExpierenceRecordConvertDTO expierenceRecordConvertDTO : expierenceRecordConvertDTOS) {
                    ExperienceRecord experienceRecord = new ExperienceRecord();
                    experienceRecord.setResumeId(resume.getId());
                    Date beginDate = Convert.toDate(expierenceRecordConvertDTO.getStartDate());
                    if(Objects.isNull(beginDate)){
                        beginDate=Convert.toDate(expierenceRecordConvertDTO.getStartDate()+"-01");
                    }
                    if(Objects.isNull(beginDate)){
                        beginDate=Convert.toDate(expierenceRecordConvertDTO.getStartDate()+"-06-01");
                    }
                    experienceRecord.setBeginDate(beginDate);
                    experienceRecord.setBeginDateString(expierenceRecordConvertDTO.getStartDate());
                    Date endDate = Convert.toDate(expierenceRecordConvertDTO.getEndDate());
                    if(Objects.isNull(endDate)){
                        endDate=Convert.toDate(expierenceRecordConvertDTO.getEndDate()+"-01");
                    }
                    if(Objects.isNull(endDate)){
                        endDate=Convert.toDate(expierenceRecordConvertDTO.getEndDate()+"-06-01");
                    }
                    experienceRecord.setEndDate(endDate);
                    experienceRecord.setBeginDateString(expierenceRecordConvertDTO.getEndDate());
                    experienceRecord.setCompanyName(expierenceRecordConvertDTO.getOrganization());
                    experienceRecord.setJob(expierenceRecordConvertDTO.getPosition());
                    experienceRecords.add(experienceRecord);
                }
                if(isPushData){
                    if(experienceRecords.size()>0){
                        experienceRecordService.saveBatch(experienceRecords);
                    }
                }
                resumeDTO.setExperienceRecordList(experienceRecords);
            }
            resumeDTO.setResume(resume);
            Object o2 = jsonArray.get(2);
            log.info("word解析出来的内容：{}",o2.toString());

        }
        AdminOperationLog adminOperationLog = new AdminOperationLog();
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            try {
                Object principal = subject.getPrincipal();
                Long adminId = Long.valueOf(principal.toString());
                adminOperationLog.setAdminId(adminId);
                adminOperationLog.setOperatorType("上传");
                adminOperationLog.setInterfaceDesc("上传简历:"+ StrUtil.blankToDefault(filename,""));
            } catch (NumberFormatException e) {
                adminOperationLog.setAdminId(0L);
            }
        }
        adminOperationLogService.save(adminOperationLog);

        return resumeDTO;
    }

    /**
     * 处理下拉框的父类问题，解决数组
     * @param downBoxDataIds  下拉框的数据和父类
     * @param map 下拉框整体数据
     */
    public static void dealArray(List<Integer> downBoxDataIds,Map<Integer,DownBoxData> map){
        if(downBoxDataIds.size()==0){
            return;
        }
        Integer id = downBoxDataIds.get(downBoxDataIds.size()-1);
        DownBoxData downBoxData = map.get(id);
        if(downBoxData==null){
            return;
        }
        Integer parentId = downBoxData.getParentId();
        if(parentId==0){
            return;
        }
        downBoxDataIds.add(parentId);
        dealArray(downBoxDataIds,map);
    }

    @Override
    public List<InputStatisticsVO> expertInputStatistics() {
        List<DownBoxData> list = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.RESUME_STATUS.getId(), ScopeEnum.RESUME.getId());
        List<CommonCountBO> commonCountBOS = resumeMapper.countByCurrentStatusId();
        Map<Long, Integer> map = commonCountBOS.stream().collect(Collectors.toMap(CommonCountBO::getId, CommonCountBO::getCount, (k1, k2) -> k2));
        ArrayList<InputStatisticsVO> result = new ArrayList<>();
        for (DownBoxData commonData : list) {
            InputStatisticsVO inputStatisticsVO = new InputStatisticsVO();
            inputStatisticsVO.setId(commonData.getId().longValue());
            inputStatisticsVO.setName(commonData.getName());
            inputStatisticsVO.setCount(map.get(commonData.getId().longValue()));
            result.add(inputStatisticsVO);
        }
        return result;
    }

    @Override
    public List<ResumeSexLevelVO> genderRate() {
        List<ResumeSexLevelVO> result = new ArrayList<>();
        List<ResumeSexLevelBO> resumeSexLevelBOList = resumeMapper.genderRate();
        // 性别是处理返回
        Map<Long, List<ResumeSexLevelBO>> levelMap = resumeSexLevelBOList.stream().collect(Collectors.groupingBy(ResumeSexLevelBO::getLevelId));

        List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_LEVEL.getId(),ScopeEnum.RESUME.getId());

        for (DownBoxData downBoxData : downBoxDataList) {
            List<ResumeSexLevelBO> resumeSexLevelBOS = levelMap.get(downBoxData.getId().longValue());
            ResumeSexLevelVO resumeSexLevelVO = new ResumeSexLevelVO();
            resumeSexLevelVO.setSex("男");
            resumeSexLevelVO.setName(downBoxData.getName());

            result.add(resumeSexLevelVO);

            ResumeSexLevelVO resumeSexLevelVOWoman = new ResumeSexLevelVO();
            resumeSexLevelVOWoman.setSex("女");
            resumeSexLevelVOWoman.setName(downBoxData.getName());

            result.add(resumeSexLevelVOWoman);

            if(Objects.nonNull(resumeSexLevelBOS)){
                Map<Integer, Integer> map = resumeSexLevelBOS.stream().collect(Collectors.toMap(ResumeSexLevelBO::getSex, ResumeSexLevelBO::getValue, (k1, k2) -> k1));
                Integer integer = map.get(1);
                if(Objects.nonNull(integer)){
                    resumeSexLevelVO.setValue(integer);
                }else {
                    resumeSexLevelVO.setValue(0);
                }
                Integer integer1 = map.get(0);
                if(Objects.nonNull(integer1)){
                    resumeSexLevelVOWoman.setValue(integer1);
                }else {
                    resumeSexLevelVOWoman.setValue(0);
                }

            }else {
                resumeSexLevelVO.setValue(0);
                resumeSexLevelVOWoman.setValue(0);
            }

        }



        return result;
    }

    @Override
    public List<ResumeAgeLevelVO> ageLevelList() {
        List<ResumeAgeLevelVO> result = new ArrayList<>();
        List<ResumeAgeLevelBO> resumeAgeLevelBOList =  resumeMapper.ageLevelList();

        List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_LEVEL.getId(),ScopeEnum.RESUME.getId());
        int year = LocalDate.now().getYear();
        Map<Integer, List<ResumeAgeLevelBO>> map = new HashMap<>();
        for (ResumeAgeLevelBO resumeAgeLevelBO : resumeAgeLevelBOList) {
            Integer birthYear = resumeAgeLevelBO.getBirthYear();
            int age = year-birthYear;
            resumeAgeLevelBO.setAge(age);
            if(age>=25 && age<75) {
                int key = age/5;
                List<ResumeAgeLevelBO> valueList = map.get(key);
                if(Objects.isNull(valueList)){
                    valueList = new ArrayList<>();
                    valueList.add(resumeAgeLevelBO);
                    map.put(key,valueList);
                }else {
                    valueList.add(resumeAgeLevelBO);
                }
            }else if(age>75){
                //大于75岁
                int key = 15;
                List<ResumeAgeLevelBO> valueList = map.get(key);
                if(Objects.isNull(valueList)){
                    valueList = new ArrayList<>();
                    valueList.add(resumeAgeLevelBO);
                    map.put(key,valueList);
                }else {
                    valueList.add(resumeAgeLevelBO);
                }
            }
        }
        for (int i=5;i<=15;i++){
            List<ResumeAgeLevelBO> resumeAgeLevelBOList1 = map.get(i);
            Map<Long, List<ResumeAgeLevelBO>> levelMap = new HashMap<>();
            if(Objects.nonNull(resumeAgeLevelBOList1)){
                //
                levelMap = resumeAgeLevelBOList1.stream().collect(Collectors.groupingBy(ResumeAgeLevelBO::getLevelId));
            }

            for (DownBoxData downBoxData : downBoxDataList) {
                ResumeAgeLevelVO resumeAgeLevelVO = new ResumeAgeLevelVO();
                resumeAgeLevelVO.setLevel(downBoxData.getName());
                int age = i*5;
                resumeAgeLevelVO.setAge(String.format("%s岁",age));
                if(Objects.nonNull(resumeAgeLevelBOList1)){
                    List<ResumeAgeLevelBO> resumeAgeLevelBOList2 = levelMap.get(downBoxData.getId().longValue());
                    if(Objects.nonNull(resumeAgeLevelBOList2)){
                        int sum = resumeAgeLevelBOList2.stream().mapToInt(ResumeAgeLevelBO::getValue).sum();
                        resumeAgeLevelVO.setValue(sum);
                    }else {
                        // 此年龄，此级别为空
                        resumeAgeLevelVO.setValue(0);
                    }
                }else {
                    resumeAgeLevelVO.setValue(0);
                }
                result.add(resumeAgeLevelVO);
            }
        }
        
        return result;
    }

    @Override
    public List<ResumeLevelAreaVO> listByArea(Long areaId, Long levelId) {
        List<ResumeLevelAreaVO> result = new ArrayList<>();
        Assert.notNull(areaId,"地区id不能为空");
        List<Area> areaList = areaService.list();
        List<Area> list = areaList.stream().filter(area -> area.getParentId().equals(areaId)).collect(Collectors.toList());
        if(list.size()==0){
            throw new MyException("所选地区下没有地区");
        }
        List<IntegerBO> resumeAreaList = resumeMapper.countByAreaId(levelId);
        for (Area area : list) {
            Long id = area.getId();
            // 需要处理这个地区下的地区做个集合
            List<Long> areaIds = new ArrayList<>();
            areaIds.add(id);
            dealAreaArray(areaIds,areaList);
            long count = resumeAreaList.stream().filter(integerBO -> areaIds.contains(integerBO.getId().longValue())).count();
            if(count!=0){
                ResumeLevelAreaVO resumeLevelAreaVO = new ResumeLevelAreaVO();
                resumeLevelAreaVO.setAreaId(area.getId());
                resumeLevelAreaVO.setAreaName(area.getName());
                resumeLevelAreaVO.setResumeCount((int) count);
                result.add(resumeLevelAreaVO);
            }
        }
        List<ResumeLevelAreaVO> sortedResult = result.stream().sorted(Comparator.comparingInt(ResumeLevelAreaVO::getResumeCount).reversed()).collect(Collectors.toList());
        return sortedResult;
    }
    private void dealAreaArray(List<Long> areaIds,List<Area> areaList) {
        List<Area> sonAreaList = areaList.stream().filter(area -> areaIds.contains(area.getParentId())&&!areaIds.contains(area.getId())).collect(Collectors.toList());
        if(sonAreaList.size()==0){
            return;
        }
        List<Long> sonAreaIdList = sonAreaList.stream().map(Area::getId).collect(Collectors.toList());
        areaIds.addAll(sonAreaIdList);
        dealAreaArray(areaIds,areaList);
    }

    @Override
    public List<Resume> listByIdsSelf(List<Long> resumeIds) {
        List<Resume> resumes = listByIds(resumeIds);
        for (Resume resume : resumes) {
            Long levelId = resume.getLevelId();
            if(Objects.nonNull(levelId)){
                DownBoxData byId = downBoxDataService.getById(levelId);
                if(Objects.nonNull(byId)){
                    resume.setLevelName(byId.getName());
                }
            }
            Date birthday = resume.getBirthday();
            if(Objects.nonNull(birthday)){
                int i = DateUtil.ageOfNow(birthday);
                resume.setAge(i);
            }
        }
        return resumes;
    }

    @Override
    public void dealDirectoryResume(String directory) {
        File file = new File(directory);
        dealDirectory(file);
    }

    public void dealDirectory(File file){
        String prefix;
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            prefix = "c://data/file/";
        }else {
            prefix = "/data/file/";
        }
        String errorPrefix;
        if(System.getProperty("os.name").toLowerCase().startsWith("win")){
            errorPrefix = "c://home/error/";
        }else {
            errorPrefix = "/home/error/";
        }
        if(!file.exists()){
            throw new MyException("文件夹不存在");
        }
        if(!file.isDirectory()){
            return;
        }
        File[] files = file.listFiles();
        LocalDate now = LocalDate.now();
        String today = now.format(DateTimeFormatter.ofPattern("yyyy-M-d"));
        if(Objects.nonNull(files)){
            for (File fileSon : files) {
                if(fileSon.isDirectory()){
                    dealDirectory(fileSon);
                }else {
                    String absolutePath = fileSon.getAbsolutePath();
                    // 把文件夹
                    StringBuilder sb = new StringBuilder();
                    String fileName = fileSon.getName();
                    long currentTimeMillis = System.currentTimeMillis();
                    String filePath = today + "/" + currentTimeMillis + "/" + fileName;
                    try {
                        FileUtil.copy(absolutePath,prefix+filePath,true);
                        // 拷贝之后需要分析
                        dealWord(filePath,16L,true,false);
                    } catch (Exception e) {
                        log.error("简历分析失败{}",fileSon.getName(),e);
                        // 分析出错的放到一个文件夹
                        FileUtil.copy(absolutePath,errorPrefix+filePath,true);
                    }
                }
            }
        }
    }
}
