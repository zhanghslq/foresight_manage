package com.zhs.backmanageb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author: zhs
 * @date: 2020/8/9 12:17
 */
@Configuration
public class HttpConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
