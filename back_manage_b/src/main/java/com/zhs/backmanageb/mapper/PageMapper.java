package com.zhs.backmanageb.mapper;

import com.zhs.backmanageb.entity.Page;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.backmanageb.model.bo.PageBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 页面表 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
public interface PageMapper extends BaseMapper<Page> {

    List<PageBO> listTree();

    List<PageBO> listByParentId(@Param("parentId") Long parentId);
}
