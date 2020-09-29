package com.zhs.controller.b.back;


import com.zhs.common.Result;
import com.zhs.entity.DownBoxType;
import com.zhs.service.DownBoxTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 新的下拉框的类别 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Api(tags = "下拉框的类型")
@RestController
@RequestMapping("/down_box_type")
public class DownBoxTypeController {
    @Resource
    private DownBoxTypeService downBoxTypeService;


    @ApiOperation(value = "查询所有下拉框类型",tags = "查询")
    @PostMapping("list")
    public Result<List<DownBoxType>> list(){
        List<DownBoxType> list = downBoxTypeService.list();

        return Result.success(list);
    }
}

