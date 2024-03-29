package com.zhs.service;

import com.zhs.entity.ConcatRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.vo.ConcatRecordVO;

/**
 * <p>
 * 联系记录表 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
public interface ConcatRecordService extends IService<ConcatRecord> {

    ConcatRecordVO queryDetail(Long id);
}
