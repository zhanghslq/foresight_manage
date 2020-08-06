package com.zhs.backmanageb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.zhs.backmanageb.common.constant.DropDownBoxTypeEnum;
import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.entity.Resume;
import com.zhs.backmanageb.mapper.ResumeMapper;
import com.zhs.backmanageb.model.vo.ResumeVO;
import com.zhs.backmanageb.service.CommonDataService;
import com.zhs.backmanageb.service.ExperienceRecordService;
import com.zhs.backmanageb.service.ResumeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.sf.jsqlparser.statement.drop.Drop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume> implements ResumeService {

    @Autowired
    private CommonDataService commonDataService;
    @Autowired
    private ExperienceRecordService experienceRecordService;

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
            resumeVOS.add(resumeVO);
            //todo 年龄，在职时间
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
}
