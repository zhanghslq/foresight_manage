package com.zhs.service;

import com.zhs.entity.DownBoxData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.bo.DownBoxDataBO;

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

    List<DownBoxDataBO> listByDownBoxTypeAndScope(Integer downBoxTypeId, Integer scopeId);

    /**
     * 返回平铺的数据
     * @param downBoxTypeId 下拉框类型
     * @param scopeId 作用域id
     * @return
     */
    List<DownBoxData> listNoTreeByDownBoxTypeAndScope(Integer downBoxTypeId, Integer scopeId);
}
