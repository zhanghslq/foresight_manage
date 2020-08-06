package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.entity.ExperienceRecord;
import com.zhs.backmanageb.mapper.ExperienceRecordMapper;
import com.zhs.backmanageb.service.ExperienceRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 履历表 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-21
 */
@Service
public class ExperienceRecordServiceImpl extends ServiceImpl<ExperienceRecordMapper, ExperienceRecord> implements ExperienceRecordService {

    @Autowired
    private ExperienceRecordMapper experienceRecordMapper;
    @Override
    public List<ExperienceRecord> queryLastExperience(List<Long> resumeIds) {
        return experienceRecordMapper.queryLastExperience(resumeIds);
    }
}
