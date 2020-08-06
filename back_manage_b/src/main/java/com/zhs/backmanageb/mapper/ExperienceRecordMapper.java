package com.zhs.backmanageb.mapper;

import com.zhs.backmanageb.entity.ExperienceRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 履历表 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-21
 */
public interface ExperienceRecordMapper extends BaseMapper<ExperienceRecord> {

    List<ExperienceRecord> queryLastExperience(@Param("resumeIds") List<Long> resumeIds);

}
