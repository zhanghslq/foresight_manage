package com.zhs.service;

import com.zhs.entity.AdminOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhs
 * @since 2020-08-04
 */
public interface AdminOperationLogService extends IService<AdminOperationLog> {

    /**
     * 导出操作记录
     * @param adminId 管理员id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param response 响应
     */
    void export(Long adminId, Date startTime, Date endTime, HttpServletResponse response);

}
