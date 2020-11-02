package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.entity.Admin;
import com.zhs.entity.AdminOperationLog;
import com.zhs.entity.ConcatRecord;
import com.zhs.exception.MyException;
import com.zhs.mapper.AdminOperationLogMapper;
import com.zhs.model.bo.AdminOperatorLogExportBO;
import com.zhs.service.AdminOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.AdminService;
import com.zhs.util.EasyExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-08-04
 */
@Service
public class AdminOperationLogServiceImpl extends ServiceImpl<AdminOperationLogMapper, AdminOperationLog> implements AdminOperationLogService {

    @Autowired
    private AdminService adminService;

    @Override
    public void export(Long adminId, Date startTime, Date endTime, HttpServletResponse response) {
        QueryWrapper<AdminOperationLog> adminOperationLogQueryWrapper = new QueryWrapper<>();
        adminOperationLogQueryWrapper.eq("admin_id",adminId);
        adminOperationLogQueryWrapper.lt("create_time",endTime);
        adminOperationLogQueryWrapper.gt("create_time",startTime);
        List<AdminOperationLog> list = list(adminOperationLogQueryWrapper);
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
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename="
                + new String(("操作记录"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
                + ".xlsx");
        ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            throw new MyException("导出出错，稍后重试");
        }
        EasyExcelUtil.writeListTo(outputStream,adminOperatorLogExportBOS, AdminOperatorLogExportBO.class);

    }
}
