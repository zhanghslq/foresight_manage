package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.entity.Area;
import com.zhs.backmanageb.mapper.AreaMapper;
import com.zhs.backmanageb.model.bo.AreaBO;
import com.zhs.backmanageb.service.AreaService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-26
 */
@Service
public class AreaServiceImpl extends ServiceImpl<AreaMapper, Area> implements AreaService {

    @Autowired
    private AreaMapper areaMapper;


    @Override
    public List<AreaBO> selectAllTree() {
        return areaMapper.selectAllTree();
    }
}
