package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.Expert;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 专家 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ExpertService extends IService<Expert> {

    void saveBatchSelf(Long classificationId, List<Expert> readBooks);
}
