package com.zhs.backmanageb.hutool;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.convert.impl.DateConverter;
import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/8/9 17:30
 */
public class ConvertTest {
    @Test
    public void test(){
        Date date = Convert.toDate("2020-2-1");
        System.out.println(date);
    }
}
