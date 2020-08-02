package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.entity.Page;
import com.zhs.backmanageb.mapper.PageMapper;
import com.zhs.backmanageb.model.bo.PageBO;
import com.zhs.backmanageb.service.PageService;
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
