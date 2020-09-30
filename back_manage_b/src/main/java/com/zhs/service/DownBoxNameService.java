package com.zhs.service;

import com.zhs.entity.DownBoxName;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.model.vo.DownBoxNameDetailVO;
import com.zhs.model.vo.DownBoxNameVO;

import java.util.List;

/**
 * <p>
 * 用户创建的下拉框名字 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
public interface DownBoxNameService extends IService<DownBoxName> {

    void add(String name, Integer typeId, List<Integer> scopeIdList);


    void updateSelf(Integer id, String name, Integer typeId, List<Integer> scopeIdList);

    void removeBySelf(Integer id);

    List<DownBoxNameVO> listAndData();


    DownBoxNameDetailVO queryById(Integer id);
}
