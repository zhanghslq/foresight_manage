package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.exception.MyException;
import com.zhs.backmanageb.mapper.CommonDataMapper;
import com.zhs.backmanageb.service.CommonDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

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

    @Override
    public void insertBatch(String content, Integer type) {
        ArrayList<CommonData> commonDataArrayList = new ArrayList<>();
        if(StringUtils.isEmpty(content)){
            throw new MyException("内容不能为空");
        }

        String[] names = content.split("\n");
        for (String name : names) {
            CommonData commonData = new CommonData();
            commonData.setName(name.trim());
            commonData.setType(type);
            commonDataArrayList.add(commonData);
        }
        saveBatch(commonDataArrayList);
    }
}
