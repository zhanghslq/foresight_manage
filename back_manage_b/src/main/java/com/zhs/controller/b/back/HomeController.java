package com.zhs.controller.b.back;

import com.zhs.common.Result;
import com.zhs.service.ContactsService;
import com.zhs.service.ExpertService;
import com.zhs.service.LeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.actuate.cache.NonUniqueCacheException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: zhs
 * @since: 2020/11/27 18:23
 */
@RequestMapping("/home")
@RestController
@Api(tags = "首页")
public class HomeController {

    @Resource
    private LeaderService leaderService;
    @Resource
    private ContactsService contactsService;
    @Resource
    private ExpertService expertService;



    @PostMapping("get/base_count")
    @ApiOperation(value = "首页的统计数据",tags = "查询")
    public Result<Object> get(){


        return Result.success(null);
    }


}
