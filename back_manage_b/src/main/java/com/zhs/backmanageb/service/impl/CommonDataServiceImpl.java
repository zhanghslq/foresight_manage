package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.mapper.CommonDataMapper;
import com.zhs.backmanageb.service.CommonDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 一些公用数据（下拉框选择的） 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class CommonDataServiceImpl extends ServiceImpl<CommonDataMapper, CommonData> implements CommonDataService {

}
