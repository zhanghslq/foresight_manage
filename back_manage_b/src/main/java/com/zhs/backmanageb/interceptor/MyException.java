package com.zhs.backmanageb.interceptor;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class MyException {
	@ExceptionHandler(value = AuthorizationException.class)
	@ResponseBody
	public String defaultAuthErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception{
		return "test";
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public String defaultErrorHandler(HttpServletRequest req, HttpServletResponse resp, Exception e) throws Exception{
		return "test";
	}

}
