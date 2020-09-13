package com.zhs.mapper;

import com.zhs.entity.Expert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.CommonCountBO;

import java.util.List;

/**
 * <p>
 * 专家 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ExpertMapper extends BaseMapper<Expert> {

    List<CommonCountBO> countByClassificationId();


}
