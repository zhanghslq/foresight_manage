package com.zhs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.entity.DownBoxScope;
import com.zhs.entity.ScopeApplication;
import com.zhs.mapper.DownBoxScopeMapper;
import com.zhs.mapper.ScopeApplicationMapper;
import com.zhs.service.DownBoxScopeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.ScopeApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 下拉框应用的可选范围 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Service
public class DownBoxScopeServiceImpl extends ServiceImpl<DownBoxScopeMapper, DownBoxScope> implements DownBoxScopeService {

    @Autowired
    private DownBoxScopeMapper downBoxScopeMapper;

    @Autowired
    private ScopeApplicationService scopeApplicationService;


    @Override
    public List<ScopeApplication> listByType(Integer typeId) {
        List<ScopeApplication> result = new ArrayList<>();
        QueryWrapper<DownBoxScope> downBoxScopeQueryWrapper = new QueryWrapper<>();
        downBoxScopeQueryWrapper.eq("down_box_id",typeId);
        List<DownBoxScope> downBoxScopeList = list(downBoxScopeQueryWrapper);
        if(downBoxScopeList.size()==0){
            return result;
        }
        List<Integer> scopeIdList = downBoxScopeList.stream().map(DownBoxScope::getScopeId).collect(Collectors.toList());
        return scopeApplicationService.listByIds(scopeIdList);
    }
}
