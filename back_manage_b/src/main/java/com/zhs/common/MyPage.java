package com.zhs.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/11 17:14
 */
@Data

public class MyPage<T> implements IPage<T> {

    @Override
    public List<OrderItem> orders() {
        return null;
    }

    @Override
    public List<T> getRecords() {
        return null;
    }

    @Override
    public IPage<T> setRecords(List<T> records) {
        return null;
    }

    @Override
    public long getTotal() {
        return 0;
    }

    @Override
    public IPage<T> setTotal(long total) {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public IPage<T> setSize(long size) {
        return null;
    }

    @Override
    public long getCurrent() {
        return 0;
    }

    @Override
    public IPage<T> setCurrent(long current) {
        return null;
    }
}
