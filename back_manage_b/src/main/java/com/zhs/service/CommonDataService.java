package com.zhs.service;

import com.zhs.entity.CommonData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 一些公用数据（下拉框选择的） 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface CommonDataService extends IService<CommonData> {

    /**
     * @param content 文本域内容
     * @param type 类型
     */
    void insertBatch(String content, Integer type);

}
