package com.zhs.mapper;

import com.zhs.entity.OrganizationType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.OrganizationTypeBO;
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
public interface OrganizationTypeMapper extends BaseMapper<OrganizationType> {

    /**
     * 查树状结构的所有数据
     * 先查最顶级的，在查下面的
     *
     * @return 树状结构的数据
     */
    List<OrganizationTypeBO> selectAllTree(@Param("type") Integer type);


    /*
     * 根据parentId查询
     * @param parentId 父级id
     * @return
     */
    List<OrganizationTypeBO> selectAllTreeByParentId(@Param("parentId") Long parentId);

    List<Long> selectAllIdHasChild();

    List<OrganizationTypeBO> selectAllHasChild(@Param("parentIds") List<Long> parentIds);

    List<OrganizationTypeBO> listByParentIdZero();

}
