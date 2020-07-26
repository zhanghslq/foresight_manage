package com.zhs.backmanageb.controller;


import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Area;
import com.zhs.backmanageb.model.bo.AreaBO;
import com.zhs.backmanageb.service.AreaService;
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
 * @since 2020-07-26
 */
@Api(tags = "地区获取")
@RestController
@RequestMapping("/area")
public class AreaController {

    @Resource
    private AreaService areaService;

    @ApiOperation("查询所有地区，树状结构")
    @PostMapping("select/all_tree")
    public Result<List<AreaBO>> selectAllTree(){
        return Result.success(areaService.selectAllTree());
    }
}

