package com.zhs.backmanageb.interceptor;

import com.alibaba.fastjson.JSON;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.common.ReturnCode;
import com.zhs.backmanageb.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {
	@ExceptionHandler(value = AuthorizationException.class)
	@ResponseBody
	public Result<String> defaultAuthErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception{
		return Result.fail(401,"无权限",e.getMessage());
	}

	@ExceptionHandler(value = MyException.class)
	@ResponseBody
	public Result<String> myErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception{
		return Result.fail(402,"数据问题",e.getMessage());
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public Result<String> defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception{
		log.error(e.getMessage(),e);
		return Result.fail(500,"服务器内部错误",e.getMessage());
	}


}
