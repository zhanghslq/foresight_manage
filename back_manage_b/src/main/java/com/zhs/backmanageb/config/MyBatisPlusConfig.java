package com.zhs.backmanageb.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther: IT贱男
 * @Date: 2019/6/12 15:06
 * @Description: MybatisPlus配置类
 */
@Configuration
public class MyBatisPlusConfig {
 
    /**
     * 分页插件
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}