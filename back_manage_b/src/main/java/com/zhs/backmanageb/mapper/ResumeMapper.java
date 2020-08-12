package com.zhs.backmanageb.mapper;

import com.zhs.backmanageb.entity.Resume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.backmanageb.model.bo.CommonCountBO;

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
