package com.zhs;

import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.entity.DownBoxData;
import com.zhs.mapper.AdminMapper;
import com.zhs.entity.Admin;
import com.zhs.mapper.OrganizationTypeMapper;
import com.zhs.service.DownBoxDataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sound.midi.Soundbank;
import java.util.List;

@Slf4j
@SpringBootTest
class BackManageBApplicationTests {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    OrganizationTypeMapper organizationTypeMapper;

    @Autowired
    private DownBoxDataService downBoxDataService;
    @Test
    void contextLoads() {
        List<Admin> admins = adminMapper.selectList(null);
        admins.forEach(System.out::println);
    }
    @Test
    void testTree(){
//        List<OrganizationTypeBO> organizationTypeBOS = organizationTypeMapper.selectAllTree();

//        System.out.println(organizationTypeBOS);
        List<DownBoxData> marketTypeList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.COMPANY_MARKET_SITUATION.getId(),null);
        System.out.println(marketTypeList);
    }

}
