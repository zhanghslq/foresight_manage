package com.zhs.service.impl;

import com.zhs.entity.Page;
import com.zhs.mapper.PageMapper;
import com.zhs.model.bo.PageBO;
import com.zhs.service.PageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 页面表 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
@Service
public class PageServiceImpl extends ServiceImpl<PageMapper, Page> implements PageService {

    @Autowired
    private PageMapper pageMapper;

    @Override
    public List<PageBO> listTree() {


        return pageMapper.listTree();
    }
}
