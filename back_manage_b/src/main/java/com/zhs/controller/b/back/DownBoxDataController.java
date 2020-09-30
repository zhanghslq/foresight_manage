package com.zhs.controller.b.back;


import com.zhs.common.Result;
import com.zhs.entity.DownBoxData;
import com.zhs.service.DownBoxDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Api(tags = "下拉框实际数据")
@RestController
@RequestMapping("/down_box_data")
public class DownBoxDataController {

    @Resource
    private DownBoxDataService downBoxDataService;


    @ApiOperation(value = "下拉框新增数据",tags = "新增")
    @PostMapping("add")
    public Result<Boolean> add(DownBoxData downBoxData){
        downBoxDataService.save(downBoxData);
        return Result.success(true);
    }
    @PostMapping("delete")
    @ApiOperation(value = "删除下拉框数据",tags = "删除")
    public Result<Boolean> delete(@RequestParam Integer id){
        downBoxDataService.removeById(id);

        return Result.success(true);
    }
    @PostMapping("update")
    @ApiOperation(value = "修改下拉框数据",tags = "修改")
    public Result<Boolean> update(DownBoxData downBoxData){

        Assert.notNull(downBoxData.getId(),"id不能为空");
        downBoxDataService.updateById(downBoxData);
        return Result.success(true);
    }


    @PostMapping("list/by_type_and_scope")
    @ApiOperation(value = "根据类型和作用域获取下拉框数据",tags = "查询")
    public Result<List<DownBoxData>> listByDownBoxTypeAndScope(@RequestParam Integer downBoxTypeId, @RequestParam Integer scopeId){
        List<DownBoxData> list = downBoxDataService.listByDownBoxTypeAndScope(downBoxTypeId,scopeId);
        return Result.success(list);
    }
}

