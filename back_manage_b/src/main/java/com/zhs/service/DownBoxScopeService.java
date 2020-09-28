package com.zhs.service;

import com.zhs.entity.DownBoxScope;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.entity.ScopeApplication;

import java.util.List;

/**
 * <p>
 * 下拉框应用的可选范围 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
public interface DownBoxScopeService extends IService<DownBoxScope> {

    List<ScopeApplication> listByType(Integer typeId);
}
