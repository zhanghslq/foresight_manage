package com.zhs.backmanageb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.zhs.backmanageb.common.constant.DropDownBoxTypeEnum;
import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.mapper.ResumeMapper;
import com.zhs.backmanageb.model.dto.ExpierenceRecordConvertDTO;
import com.zhs.backmanageb.model.dto.ResumeConvertDTO;
import com.zhs.backmanageb.model.dto.ResumeDTO;
import com.zhs.backmanageb.model.vo.ResumeVO;
import com.zhs.backmanageb.service.CommonDataService;
import com.zhs.backmanageb.service.ExperienceRecordService;
import com.zhs.backmanageb.service.ResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.backmanageb.util.AsposeWordUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.drop.Drop;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

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
    private CommonDataService commonDataService;
    @Autowired
    private ExperienceRecordService experienceRecordService;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Page<ResumeVO> pageSelf(Page<Resume> resumePage) {
        QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
        commonDataQueryWrapper.eq("type", DropDownBoxTypeEnum.RESUME_LEVEL.getId());
        List<CommonData> list = commonDataService.list(commonDataQueryWrapper);
        Map<Long, String> map = list.stream().collect(Collectors.toMap(CommonData::getId, CommonData::getName));
        List<ResumeVO> resumeVOS = new ArrayList<>();
        Page<ResumeVO> resumeVOPage = new Page<>();
        Page<Resume> page = page(resumePage);
        // 对page进行处理
        List<Resume> records = page.getRecords();
        List<Long> resumeIds = records.stream().map(Resume::getId).collect(Collectors.toList());
        List<ExperienceRecord>  lastExperienceList = experienceRecordService.queryLastExperience(resumeIds);
        // 工作时间
        Map<Long, Date> workMap = lastExperienceList.stream().collect(Collectors.toMap(ExperienceRecord::getResumeId, ExperienceRecord::getBeginDate));
        BeanUtil.copyProperties(page,resumeVOPage);
        for (Resume record : records) {
            ResumeVO resumeVO = new ResumeVO();
            BeanUtil.copyProperties(record,resumeVO);
            // 然后把字段值填上
            // 行政级别
            resumeVO.setLevelName(map.get(resumeVO.getLevelId()));
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
            resumeVOS.add(resumeVO);
        }
        resumeVOPage.setRecords(resumeVOS);
        return resumeVOPage;
    }

    @Override
    public ResumeDTO dealWord(String filename) {
        ResumeDTO resumeDTO = new ResumeDTO();

        String text = AsposeWordUtil.getText(filename);
        // 拿到text到php进行请求，获取结果

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        //定义请求参数类型，这里用json所以是MediaType.APPLICATION_JSON
        headers.setContentType(MediaType.APPLICATION_JSON);
        //RestTemplate带参传的时候要用HttpEntity<?>对象传递
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("resumeText", text);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> entity = restTemplate.postForEntity("http://resume.carltrip.com/api/resume/index", request, String.class);
        //获取3方接口返回的数据通过entity.getBody();它返回的是一个字符串；
        String body = entity.getBody();
        if(!StringUtils.isEmpty(body)){
            JSONArray jsonArray = JSONArray.parseArray(body);
            /*
             * 0=个人信息
             * 1=履历
             * 2=传入的内容
             */
            Object o = jsonArray.get(0);
            ResumeConvertDTO resumeConvertDTO = JSONObject.parseObject(o.toString(), ResumeConvertDTO.class);
            Resume resume = new Resume();
            BeanUtil.copyProperties(resumeConvertDTO,resume);
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
            resume.setBirthday(date);
            resume.setBirthdayString(birthday);
            resume.setJob(resumeConvertDTO.getPosition());
            resume.setOrganization(resumeConvertDTO.getPoliticsCompany());
            resume.setOrganizationJob(resumeConvertDTO.getPoliticsPosition());

            Object o1 = jsonArray.get(1);
            List<ExpierenceRecordConvertDTO> expierenceRecordConvertDTOS = JSONArray.parseArray(o1.toString(), ExpierenceRecordConvertDTO.class);
            ArrayList<ExperienceRecord> experienceRecords = new ArrayList<>();
            for (ExpierenceRecordConvertDTO expierenceRecordConvertDTO : expierenceRecordConvertDTOS) {
                ExperienceRecord experienceRecord = new ExperienceRecord();
                experienceRecord.setBeginDate(Convert.toDate(expierenceRecordConvertDTO.getStartDate()));
                experienceRecord.setBeginDateString(expierenceRecordConvertDTO.getStartDate());
                experienceRecord.setEndDate(Convert.toDate(expierenceRecordConvertDTO.getEndDate()));
                experienceRecord.setBeginDateString(expierenceRecordConvertDTO.getEndDate());
                experienceRecord.setCompanyName(expierenceRecordConvertDTO.getPosition());
                experienceRecords.add(experienceRecord);
            }
            resumeDTO.setExperienceRecordList(experienceRecords);
            resumeDTO.setResume(resume);

            Object o2 = jsonArray.get(2);
            log.info("word解析出来的内容：{}",o2.toString());

        }
        return resumeDTO;
    }
}
