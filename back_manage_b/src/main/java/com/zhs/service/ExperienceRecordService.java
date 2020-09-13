package com.zhs.service;

import com.zhs.entity.ExperienceRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 履历表 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-21
 */
public interface ExperienceRecordService extends IService<ExperienceRecord> {

    List<ExperienceRecord> queryLastExperience(List<Long> resumeIds);
}
