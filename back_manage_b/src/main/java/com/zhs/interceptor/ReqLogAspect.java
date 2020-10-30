package com.zhs.interceptor;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;


import com.zhs.entity.Admin;
import com.zhs.entity.AdminOperationLog;
import com.zhs.service.AdminOperationLogService;
import com.zhs.service.AdminService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 */
@Aspect
@Order(1)
@Component
@Slf4j
public class ReqLogAspect {
    @Autowired
    private AdminOperationLogService adminOperationLogService;
    @Autowired
    private AdminService adminService;
    

    @Pointcut("execution(public * com.zhs.controller..*.*(..))")
    public void webLog() {
    }

    ThreadLocal<Long> startTime = new ThreadLocal<>();
    ThreadLocal<String> requestInfo = new ThreadLocal<>();
    ThreadLocal<Long> logId = new ThreadLocal<>();


    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        AdminOperationLog adminOperationLog = new AdminOperationLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        requestInfo.set(request.getRequestURL().toString());

        String requestURL = request.getRequestURL().toString();
        adminOperationLog.setInterfaceAllName(requestURL);
        String ip = getIp(request);
        adminOperationLog.setIp(ip);
        // 全路径，带url
        String queryString = request.getQueryString();
        log.info("queryString:{}",queryString);
        adminOperationLog.setParams(queryString);
        String servletPath = request.getServletPath();
        log.info("servletPath:{}",servletPath);
        adminOperationLog.setInterfaceName(servletPath);
        MethodSignature signature= (MethodSignature) joinPoint.getSignature();
        ApiOperation annotation = signature.getMethod().getAnnotation(ApiOperation.class);
        if(annotation!=null){
            adminOperationLog.setInterfaceDesc(annotation.value());
            String[] tags = annotation.tags();
            if(tags.length>0){
                adminOperationLog.setOperatorType(tags[0]);
                if("登陆".equals(tags[0])){
                    String username = request.getParameter("username");
                    // 登陆的时候可以根据username,进行查询
                    Admin admin = adminService.queryByUserName(username);
                    if(!Objects.isNull(admin)){
                        adminOperationLog.setAdminId(admin.getId());
                    }
                }
            }
        }
        // 请求类名
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        log.info("declaringTypeName:{}",declaringTypeName);
        adminOperationLog.setDeclaringName(declaringTypeName);
        // 请求方法名
        String name = joinPoint.getSignature().getName();

        adminOperationLog.setMethodName(name);
        // body类型参数
        String body = JSONUtil.toJsonStr(joinPoint.getArgs());
        if(body.length()>5000){
            body=body.substring(0,4999);
        }
        adminOperationLog.setBody(body);

        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            try {
                Object principal = subject.getPrincipal();
                Long adminId = Long.valueOf(principal.toString());
                adminOperationLog.setAdminId(adminId);
            } catch (NumberFormatException e) {
                adminOperationLog.setAdminId(0L);
            }
        }

        // 组织和简历的单独记录
        if(!"查询".equals(adminOperationLog.getOperatorType())&&
                !"com.zhs.controller.b.back.OrganizationController".equals(adminOperationLog.getDeclaringName())
                &&!"com.zhs.controller.b.back.OrganizationController".equals(adminOperationLog.getDeclaringName())
                &&!"com.zhs.controller.b.back.OrganizationModuleController".equals(adminOperationLog.getDeclaringName())
        ){
            adminOperationLogService.save(adminOperationLog);
            logId.set(adminOperationLog.getId());
        }

        if("新增".equals(adminOperationLog.getOperatorType())||"修改".equals(adminOperationLog.getOperatorType())||"删除".equals(adminOperationLog.getOperatorType())){
            adminService.addOperatorCount(adminOperationLog.getAdminId());
        }
        log.info(adminOperationLog.toString());
        //记录基本信息
        log.info(requestInfo.get() + " BASIC " +   requestURL + " " + joinPoint.getSignature().getDeclaringTypeName());
        log.info(requestInfo.get() + " ARGS " + printArray(joinPoint.getArgs()) + " " + getParams(request));
    }
    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Object ret) {
        Long adminOperatorId = logId.get();
        long useTime = System.currentTimeMillis() - startTime.get();
        if(!Objects.isNull(adminOperatorId)){
            String returnString = JSONObject.toJSONString(ret);
            if(returnString.length()>5000){
                returnString=returnString.substring(0,4999);
            }
            AdminOperationLog byId = adminOperationLogService.getById(adminOperatorId);
            byId.setReturnString(returnString);
            byId.setUseTime(useTime);

            adminOperationLogService.updateById(byId);
        }
        // 记录返回结果
        log.info(requestInfo.get() + " RETURN " + (useTime) + " " + JSONObject.toJSONString(ret));
        startTime.remove();
        requestInfo.remove();
        logId.remove();
    }


    @AfterThrowing(pointcut = "webLog()", throwing = "e")
    public void doAfterThrowing(Exception e) {
        Long adminOperatorId = logId.get();
        long useTime = System.currentTimeMillis() - startTime.get();
        AdminOperationLog byId = adminOperationLogService.getById(adminOperatorId);
        log.error("报错",e);
        byId.setReturnString(e.getMessage());
        byId.setUseTime(useTime);
        adminOperationLogService.updateById(byId);
        log.warn(requestInfo.get() + " THROWERROR OtherException " + useTime , e);
        startTime.remove();
        requestInfo.remove();
        logId.remove();
    }

    private String getParams(HttpServletRequest request) {
        Map<String,String> map = new HashMap<>();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                if (paramValue.length() != 0) {
                    map.put(paramName, paramValue);
                }
            }
        }
        return map.toString();

    }

    private String printArray(Object[] objs){
        StringBuilder rt = new StringBuilder("[");
        int count = 0;
        for(Object o : objs){
            if(count>0){
                rt.append(",");
            }
            if(o!=null && o.getClass()!=null && o.getClass().isArray()){
                rt.append(Arrays.toString((Object[]) o));
            }else {
                if (o!=null && o.toString().length()>1200){
                    rt.append(o.toString(), 0, 1000);
                }else {
                    rt.append(o);
                }
            }
            count++;
        }
        return rt+"]";
    }

}
