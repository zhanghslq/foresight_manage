package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.entity.DownBoxData;
import com.zhs.entity.DownBoxName;
import com.zhs.entity.DownBoxScopeReal;
import com.zhs.mapper.DownBoxNameMapper;
import com.zhs.model.vo.DownBoxNameVO;
import com.zhs.service.DownBoxDataService;
import com.zhs.service.DownBoxNameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.DownBoxScopeRealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户创建的下拉框名字 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Service
public class DownBoxNameServiceImpl extends ServiceImpl<DownBoxNameMapper, DownBoxName> implements DownBoxNameService {

    @Autowired
    private DownBoxScopeRealService downBoxScopeRealService;

    @Autowired
    private DownBoxDataService downBoxDataService;

    @Override
    public void add(String name, Integer typeId, List<Integer> scopeIdList) {
        Assert.notEmpty(scopeIdList,"作用范围不能为空");

        //插入自定义数据
        DownBoxName downBoxName = new DownBoxName();
        downBoxName.setName(name).setDownBoxTypeId(typeId);
        save(downBoxName);
        Integer downBoxNameId = downBoxName.getId();
        //插入对应的作用范围
        List<DownBoxScopeReal> downBoxScopeReals = new ArrayList<>();
        for (Integer scopeId : scopeIdList) {
            DownBoxScopeReal downBoxScopeReal = new DownBoxScopeReal();
            downBoxScopeReal.setDownBoxNameId(downBoxNameId);
            downBoxScopeReal.setScopeId(scopeId);
            downBoxScopeReals.add(downBoxScopeReal);
        }
        downBoxScopeRealService.saveBatch(downBoxScopeReals);
    }

    @Override
    public void updateSelf(Integer id, String name, Integer typeId, List<Integer> scopeIdList) {
        DownBoxName downBoxName = new DownBoxName();
        downBoxName.setName(name).setDownBoxTypeId(typeId).setId(id);
        updateById(downBoxName);
        QueryWrapper<DownBoxScopeReal> downBoxScopeRealQueryWrapper = new QueryWrapper<>();
        downBoxScopeRealQueryWrapper.eq("down_box_name_id",id);
        List<DownBoxScopeReal> list = downBoxScopeRealService.list(downBoxScopeRealQueryWrapper);
        List<DownBoxScopeReal> downBoxScopeReals = new ArrayList<>();
        if(list.size()>0){
            List<Integer> collect = list.stream().map(DownBoxScopeReal::getScopeId).collect(Collectors.toList());
            scopeIdList.removeAll(collect);
        }
        for (Integer scopeId : scopeIdList) {
            DownBoxScopeReal downBoxScopeReal = new DownBoxScopeReal();
            downBoxScopeReal.setDownBoxNameId(id);
            downBoxScopeReal.setScopeId(scopeId);
            downBoxScopeReals.add(downBoxScopeReal);
        }
        downBoxScopeRealService.saveBatch(downBoxScopeReals);
    }

    @Override
    public void removeBySelf(Integer id) {
        removeById(id);
        QueryWrapper<DownBoxScopeReal> downBoxScopeRealQueryWrapper = new QueryWrapper<>();
        downBoxScopeRealQueryWrapper.eq("down_box_name_id",id);
        downBoxScopeRealService.remove(downBoxScopeRealQueryWrapper);
    }

    @Override
    public List<DownBoxNameVO> listAndData() {
        List<DownBoxName> downBoxNameList = list();
        List<DownBoxData> downBoxDataList = downBoxDataService.list();
        Map<Integer, List<DownBoxData>> map = downBoxDataList.stream().collect(Collectors.groupingBy(DownBoxData::getType));
        List<DownBoxNameVO> downBoxNameVOList = new ArrayList<>();
        for (DownBoxName downBoxName : downBoxNameList) {
            DownBoxNameVO downBoxNameVO = new DownBoxNameVO();
            BeanUtil.copyProperties(downBoxName,downBoxNameVO);
            List<DownBoxData> list = map.get(downBoxName.getId());
            if(!Objects.isNull(list)){
                List<DownBoxData> sortedResult = list.stream().sorted(Comparator.comparingInt(DownBoxData::getSeq)).collect(Collectors.toList());
                downBoxNameVO.setDownBoxDataList(sortedResult);
            }
            downBoxNameVOList.add(downBoxNameVO);

        }
        return downBoxNameVOList;
    }
}
