package com.zhs.service;

import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.entity.DownBoxData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/11/5 17:16
 */
@SpringBootTest
public class DownDataTest {
    @Autowired
    private DownBoxDataService downBoxDataService;


    @Test
    public void test(){
        List<DownBoxData> downBoxData = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_LEVEL.getId(), null);

        for (DownBoxData downBoxDatum : downBoxData) {
            System.out.println(downBoxData);
        }
    }
}
