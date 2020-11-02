package com.zhs.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.entity.Admin;
import com.zhs.entity.AdminOperationLog;
import com.zhs.exception.MyException;
import com.zhs.model.bo.AdminOperatorLogExportBO;
import com.zhs.util.EasyExcelUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: zhs
 * @since: 2020/11/2 14:17
 */
@SpringBootTest

public class TestExcelExport {
    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @Test
    public void test1(){
        System.out.println(LocalDate.MAX);
    }
    @Test
    public void test() throws FileNotFoundException {
        QueryWrapper<AdminOperationLog> adminOperationLogQueryWrapper = new QueryWrapper<>();
        adminOperationLogQueryWrapper.eq("admin_id",20);


        List<AdminOperationLog> list = adminOperationLogService.list(adminOperationLogQueryWrapper);
        List<Long> adminIdList = list.stream().map(AdminOperationLog::getAdminId).collect(Collectors.toList());
        Map<Long, Admin> adminHashMap = new HashMap<>();
        if(adminIdList.size()>0){
            List<Admin> admins = adminService.listByIds(adminIdList);
            adminHashMap = admins.stream().collect(Collectors.toMap(Admin::getId, admin -> admin, (k1, k2) -> k1));
        }

        ArrayList<AdminOperatorLogExportBO> adminOperatorLogExportBOS = new ArrayList<>();
        for (AdminOperationLog adminOperationLog : list) {
            AdminOperatorLogExportBO adminOperatorLogExportBO = new AdminOperatorLogExportBO();
            BeanUtil.copyProperties(adminOperationLog,adminOperatorLogExportBO);
            Admin admin = adminHashMap.get(adminOperationLog.getAdminId());
            if(Objects.nonNull(admin)){
                adminOperatorLogExportBO.setAdminName(admin.getRealName());
            }
            adminOperatorLogExportBOS.add(adminOperatorLogExportBO);
        }
        FileOutputStream fileOutputStream = new FileOutputStream("D://test.xlsx");
        EasyExcelUtil.writeListTo(fileOutputStream,adminOperatorLogExportBOS, AdminOperatorLogExportBO.class);
    }
}
