package com.zhs.test;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author: zhs
 * @since: 2020/11/17 10:02
 */
public class DateTest {
    @Test
    public void test(){
        DateTime beginOfToday = DateUtil.beginOfDay(new Date());
        System.out.println(beginOfToday);
    }
}
