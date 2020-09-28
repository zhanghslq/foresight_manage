package com.zhs.service;

import com.zhs.entity.DownBoxData;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
public interface DownBoxDataService extends IService<DownBoxData> {

    List<DownBoxData> listByDownBoxTypeAndScope(Integer downBoxTypeId, Integer scopeId);
}
