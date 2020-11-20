package com.zhs.service;

import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.ScopeEnum;
import com.zhs.entity.DownBoxType;
import com.zhs.model.bo.DownBoxDataBO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author: zhs
 * @since: 2020/11/17 17:19
 */
@SpringBootTest
public class CompanyServiceTest {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private DownBoxDataService downBoxDataService;
    @Test
    public void test(){
        List<DownBoxDataBO> downBoxDataBOS = downBoxDataService.listByDownBoxTypeAndScope(DownBoxTypeEnum.COMPANY_TYPE.getId(), ScopeEnum.COMPANY.getId());
        System.out.println(downBoxDataBOS);
    }
}
