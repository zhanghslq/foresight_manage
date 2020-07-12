package com.zhs.backmanageb.controller;


import com.zhs.backmanageb.service.LeaderService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 领导人 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "领导管理")
@RestController
@RequestMapping("/leader")
public class LeaderController {
    @Resource
    private LeaderService leaderService;




}

