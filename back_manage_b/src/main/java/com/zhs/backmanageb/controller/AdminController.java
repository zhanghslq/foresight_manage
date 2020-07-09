package com.zhs.backmanageb.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: zhs
 * @date: 2020/6/9 18:57
 */
@RequestMapping("/admin")
@RestController
public class AdminController {

    @RequestMapping("/login")
    public void login(){
        System.out.println("login");
    }
    @RequestMapping("/search")
    public String search(){

        return "";
    }
}
