package com.zhs.service;

import com.zhs.model.bo.OrganizationTypeBO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author: zhs
 * @date: 2020/7/23 17:20
 */
@SpringBootTest
public class OrganizationServiceTest {
    @Autowired
    OrganizationTypeService organizationTypeService;

    @Test
    public void testListAll(){
        List<OrganizationTypeBO> commonTypeVOS =organizationTypeService.listAll();
        System.out.println(commonTypeVOS);
    }
}
