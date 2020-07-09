package com.zhs.backmanageb.controller;

import com.zhs.backmanageb.model.TestModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhs
 * @date: 2020/6/1 10:43
 */
@RestController
@RequestMapping("/test")
@Api(value = "第一个测试接口",tags = "测试的controller")
public class TestController {


    @ApiOperation("添加方法")
    @PostMapping("/add")
    @RequiresPermissions({"user:add"})
    public void add(String username,String password){
        System.out.println(username);
        System.out.println(password);
    }
    @GetMapping("/get")
    @ApiOperation("获取方法")
    public TestModel get(){
        return new TestModel();
    }
}
