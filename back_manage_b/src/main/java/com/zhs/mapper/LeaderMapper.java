package com.zhs.mapper;

import com.zhs.entity.Leader;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.LongBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 领导人 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface LeaderMapper extends BaseMapper<Leader> {

    List<LongBO> countByOrganizationId(@Param("organizationIdList") List<Long> organizationIdList);

}
