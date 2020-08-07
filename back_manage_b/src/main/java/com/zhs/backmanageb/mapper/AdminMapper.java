package com.zhs.backmanageb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhs.backmanageb.entity.Admin;
import org.apache.ibatis.annotations.Param;

/**
 * @author: zhs
 * @date: 2020/7/6 15:32
 */
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 增加登陆次数
     * @param id
     */
    void increaseLoginCount(@Param("id") Long id);
}
