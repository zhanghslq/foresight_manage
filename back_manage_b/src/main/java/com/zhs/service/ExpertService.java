package com.zhs.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.entity.Expert;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.vo.InputStatisticsVO;

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

    List<InputStatisticsVO> expertInputStatistics();

    Page<Expert> listContactExpert(Long adminId, Integer current, Integer size);
}
