package com.zhs.controller.b.back;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.common.Result;
import com.zhs.entity.AdminOperationLog;
import com.zhs.model.vo.AdminOperationLogVO;
import com.zhs.service.AdminOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-08-04
 */
@Api(tags = "操作历史")
@RestController
@RequestMapping("/adminOperationLog")
public class AdminOperationLogController {
    @Resource
    private AdminOperationLogService adminOperationLogService;

    @ApiOperation(value = "查询操作历史",tags = "查询")
    @PostMapping("query/by_admin_id")
    public Result<Page<AdminOperationLogVO>> queryByAdminId(@RequestParam Long adminId, Integer page, Integer size){
        Page<AdminOperationLog> adminOperationLogPage = new Page<>(page,size);

        QueryWrapper<AdminOperationLog> adminOperationLogQueryWrapper = new QueryWrapper<>();
        adminOperationLogQueryWrapper.eq("admin_id",adminId);
        adminOperationLogQueryWrapper.orderByDesc("id");
        Page<AdminOperationLog> page1 = adminOperationLogService.page(adminOperationLogPage, adminOperationLogQueryWrapper);
        Page<AdminOperationLogVO> adminOperationLogVOPage = new Page<>();
        BeanUtil.copyProperties(page1,adminOperationLogVOPage);
        List<AdminOperationLog> records = page1.getRecords();
        List<AdminOperationLogVO> result = new ArrayList<>();
        for (AdminOperationLog record : records) {
            AdminOperationLogVO adminOperationLogVO = new AdminOperationLogVO();
            BeanUtil.copyProperties(record,adminOperationLogVO);
            result.add(adminOperationLogVO);
        }
        adminOperationLogVOPage.setRecords(result);
        return Result.success(adminOperationLogVOPage);
    }
    @ApiOperation(value = "导出操作历史",tags = "查询")
    @PostMapping("export/admin_operator_log")
    public void export(@RequestParam Long adminId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime, HttpServletResponse response){
        adminOperationLogService.export(adminId,startTime,endTime,response);
    }

}

