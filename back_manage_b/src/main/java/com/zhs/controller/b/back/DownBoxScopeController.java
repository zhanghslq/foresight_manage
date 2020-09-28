package com.zhs.controller.b.back;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.Result;
import com.zhs.entity.DownBoxScope;
import com.zhs.entity.DownBoxType;
import com.zhs.entity.ScopeApplication;
import com.zhs.service.DownBoxScopeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 下拉框应用的可选范围 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Api(tags = "下拉框作用范围选择")
@RestController
@RequestMapping("/downBoxScope")
public class DownBoxScopeController {
    @Resource
    private DownBoxScopeService downBoxScopeService;

    @ApiOperation(value = "根据下拉框类型查询可选作用域",tags = "查询")
    @PostMapping("list/by_type")
    public Result<List<ScopeApplication>> listByType(@RequestParam Integer typeId){
        List<ScopeApplication> list = downBoxScopeService.listByType(typeId);
        return Result.success(list);
    }
}

