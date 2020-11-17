package com.zhs.mapper;

import cn.hutool.core.date.DateTime;
import com.zhs.entity.Resume;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.*;
import com.zhs.model.vo.ResumeSexLevelVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ResumeMapper extends BaseMapper<Resume> {

    List<CommonCountBO> countByCurrentStatusId();

    List<ResumeSexLevelBO> genderRate();

    List<ResumeAgeLevelBO> ageLevelList();

    List<IntegerBO> countByAreaId(@Param("levelId") Long levelId);

    List<AdminCountBO> countByAdminId(@Param("adminIdList") List<Long> adminIdList, @Param("beginOfToday") DateTime beginOfToday);

}
