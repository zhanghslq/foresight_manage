package com.zhs.mapper;

import com.zhs.entity.Area;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.AreaBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-26
 */
public interface AreaMapper extends BaseMapper<Area> {

    List<AreaBO> selectAllTree();

    List<AreaBO> selectByParentId(@Param("parentId") Long parentId);

}
