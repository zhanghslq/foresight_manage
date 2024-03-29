package com.zhs.mapper;

import cn.hutool.core.date.DateTime;
import com.zhs.entity.Organization;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.AdminCountBO;
import com.zhs.model.bo.OrganizationHasParentBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 组织机构 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface OrganizationMapper extends BaseMapper<Organization> {

    OrganizationHasParentBO listParentById(@Param("organizationId") Long organizationId);

    List<AdminCountBO> countByAdminId(@Param("adminIdList") List<Long> adminIdList, @Param("beginOfToday") DateTime beginOfToday);
}
