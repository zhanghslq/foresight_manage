package com.zhs.backmanageb.service;

import com.zhs.backmanageb.entity.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhs.backmanageb.model.bo.PageBO;

import java.util.List;

/**
 * <p>
 * 页面表 服务类
 * </p>
 *
 * @author zhs
 * @since 2020-07-29
 */
public interface PageService extends IService<Page> {

    List<PageBO> listTree();
}
