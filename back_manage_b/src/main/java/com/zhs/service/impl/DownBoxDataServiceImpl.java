package com.zhs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.entity.DownBoxData;
import com.zhs.entity.DownBoxName;
import com.zhs.entity.DownBoxScopeReal;
import com.zhs.mapper.DownBoxDataMapper;
import com.zhs.service.DownBoxDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.DownBoxNameService;
import com.zhs.service.DownBoxScopeRealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Service
public class DownBoxDataServiceImpl extends ServiceImpl<DownBoxDataMapper, DownBoxData> implements DownBoxDataService {

    @Autowired
    @Lazy
    private DownBoxNameService downBoxNameService;

    @Autowired
    @Lazy
    private DownBoxScopeRealService downBoxScopeRealService;



    @Override
    public List<DownBoxData> listByDownBoxTypeAndScope(Integer downBoxTypeId, Integer scopeId) {
        List<DownBoxData> result = new ArrayList<>();
        // 查
        QueryWrapper<DownBoxName> downBoxNameQueryWrapper = new QueryWrapper<>();
        downBoxNameQueryWrapper.eq("down_box_type_id",downBoxTypeId);
        // 这个类型下下拉框名称
        List<DownBoxName> downBoxNameList = downBoxNameService.list(downBoxNameQueryWrapper);
        if(downBoxNameList.size()==0){
            return result;
        }
        List<Integer> downBoxNameIdList = downBoxNameList.stream().map(DownBoxName::getId).collect(Collectors.toList());
        // 根据作用域查
        QueryWrapper<DownBoxScopeReal> downBoxScopeRealQueryWrapper = new QueryWrapper<>();
        downBoxScopeRealQueryWrapper.eq("scope_id",scopeId);
        downBoxScopeRealQueryWrapper.in("down_box_name_id",downBoxNameIdList);
        List<DownBoxScopeReal> downBoxScopeRealList = downBoxScopeRealService.list(downBoxScopeRealQueryWrapper);
        if(downBoxScopeRealList.size()==0){
            return result;
        }
        // 双重查询的结果
        List<Integer> downNameIdList = downBoxScopeRealList.stream().map(DownBoxScopeReal::getDownBoxNameId).collect(Collectors.toList());
        List<DownBoxData> downBoxDataList = listByIds(downNameIdList);
        result.addAll(downBoxDataList);
        return result;
    }
}
