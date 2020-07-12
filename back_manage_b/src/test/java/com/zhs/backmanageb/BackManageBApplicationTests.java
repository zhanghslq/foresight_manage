package com.zhs.backmanageb;

import com.zhs.backmanageb.mapper.AdminMapper;
import com.zhs.backmanageb.entity.Admin;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest
class BackManageBApplicationTests {
    @Autowired
    private AdminMapper adminMapper;

    @Test
    void contextLoads() {
        List<Admin> admins = adminMapper.selectList(null);
        admins.forEach(System.out::println);
    }

}
