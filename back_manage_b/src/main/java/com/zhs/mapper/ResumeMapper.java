package com.zhs.mapper;

import com.zhs.entity.Resume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.CommonCountBO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ResumeMapper extends BaseMapper<Resume> {

    List<CommonCountBO> countByCurrentStatusId();

}
