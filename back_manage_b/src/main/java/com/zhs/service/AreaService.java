package com.zhs.service;

import com.zhs.entity.Area;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.bo.AreaBO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-26
 */
public interface AreaService extends IService<Area> {

    List<AreaBO> selectAllTree();
}
