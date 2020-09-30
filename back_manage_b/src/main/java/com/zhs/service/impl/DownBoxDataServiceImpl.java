package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.entity.DownBoxData;
import com.zhs.entity.DownBoxName;
import com.zhs.entity.DownBoxScopeReal;
import com.zhs.mapper.DownBoxDataMapper;
import com.zhs.model.bo.DownBoxDataBO;
import com.zhs.service.DownBoxDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.DownBoxNameService;
import com.zhs.service.DownBoxScopeRealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public List<DownBoxDataBO> listByDownBoxTypeAndScope(Integer downBoxTypeId, Integer scopeId) {
        List<DownBoxDataBO> result = new ArrayList<>();
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
        QueryWrapper<DownBoxData> downBoxDataQueryWrapper = new QueryWrapper<>();
        downBoxDataQueryWrapper.in("type",downNameIdList);
        List<DownBoxData> downBoxDataList = list(downBoxDataQueryWrapper);

        // 需要对数据处理，做成树状结构的,递归处理
        Map<Integer, List<DownBoxData>> map  = downBoxDataList.stream().collect(Collectors.groupingBy(DownBoxData::getParentId));
        // 初始的下拉框数据
        List<DownBoxData> downBoxDataParent = map.get(0);
        if(Objects.isNull(downBoxDataParent)||downBoxDataParent.size()==0){
            return result;
        }
        for (DownBoxData downBoxData : downBoxDataParent) {
            DownBoxDataBO downBoxDataBO = new DownBoxDataBO();
            BeanUtil.copyProperties(downBoxData,downBoxDataBO);
            result.add(downBoxDataBO);
        }
        dealDownBoxDataTree(map,result);
        return result;
    }

    private void dealDownBoxDataTree(Map<Integer, List<DownBoxData>> map, List<DownBoxDataBO> result) {
        for (DownBoxDataBO downBoxDataBO : result) {
            List<DownBoxData> downBoxDataList = map.get(downBoxDataBO.getId());
            if(Objects.isNull(downBoxDataList)||downBoxDataList.size()==0){
                // 无子类，直接过到下一个
                continue;
            }
            List<DownBoxDataBO> subList = new ArrayList<>();
            for (DownBoxData downBoxData : downBoxDataList) {
                DownBoxDataBO downBoxBO = new DownBoxDataBO();
                BeanUtil.copyProperties(downBoxData,downBoxBO);
                subList.add(downBoxBO);
            }
            dealDownBoxDataTree(map,subList);
            downBoxDataBO.setChildren(subList);
        }
    }
}
