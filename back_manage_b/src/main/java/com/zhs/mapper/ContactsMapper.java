package com.zhs.mapper;

import com.zhs.entity.Contacts;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.model.bo.LongBO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 联系人 Mapper 接口
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ContactsMapper extends BaseMapper<Contacts> {

    List<LongBO> countByOrganizationId(@Param("organizationIdList") List<Long> organizationIdList);

}
