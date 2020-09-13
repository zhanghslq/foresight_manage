package com.zhs;

import com.zhs.mapper.AdminMapper;
import com.zhs.entity.Admin;
import com.zhs.mapper.OrganizationTypeMapper;
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
    @Autowired
    OrganizationTypeMapper organizationTypeMapper;

    @Test
    void contextLoads() {
        List<Admin> admins = adminMapper.selectList(null);
        admins.forEach(System.out::println);
    }
    @Test
    void testTree(){
//        List<OrganizationTypeBO> organizationTypeBOS = organizationTypeMapper.selectAllTree();

//        System.out.println(organizationTypeBOS);
    }

}
