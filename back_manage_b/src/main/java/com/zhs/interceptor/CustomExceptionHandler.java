package com.zhs.interceptor;

import com.zhs.common.Result;
import com.zhs.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
