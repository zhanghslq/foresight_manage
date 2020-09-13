package com.zhs.cron;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: zhs
 * @date: 2020/7/30 10:27
 */
@Component
@Slf4j
public class TestCron {

//    @Scheduled(cron = "*/2 * * * * ?")
    public void testLog(){
        for (int i = 0; i < 10000; i++) {
            log.info(new Date() +"测试");
        }
    }
}
