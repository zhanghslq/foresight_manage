package com.zhs.controller.b.front;


import com.zhs.entity.Region;
import com.zhs.service.RegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-10-30
 */
@RestController
@RequestMapping("/region")
public class RegionController {
    @Resource
    private RegionService regionService;

    @ApiOperation(value = "查询全部地区",tags = "查询")
    @PostMapping("list")
    public List<Region> list(){
        return regionService.list();
    }

}

