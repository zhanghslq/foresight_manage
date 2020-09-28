package com.zhs.controller.b.back;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.Result;
import com.zhs.entity.DownBoxName;
import com.zhs.model.vo.DownBoxNameVO;
import com.zhs.service.DownBoxNameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户创建的下拉框名字 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Api(tags = "自定义系统配置数据")
@RestController
@RequestMapping("/downBoxName")
public class DownBoxNameController {

    @Resource
    private DownBoxNameService downBoxNameService;

    @ApiOperation(value = "添加自定义菜单",tags = "新增")
    @PostMapping("add")
    public Result<Boolean> add(@RequestParam String name, Integer parentId, @RequestParam Integer typeId,@RequestParam List<Integer> scopeIdList) {
        downBoxNameService.add(name,parentId,typeId,scopeIdList);
        return Result.success(true);
    }

    @ApiOperation(value = "修改自定义下拉框",tags = "修改")
    @PostMapping("update")
    public Result<Boolean> update(@RequestParam Integer id,@RequestParam String name, Integer parentId, @RequestParam Integer typeId,@RequestParam List<Integer> scopeIdList){
        downBoxNameService.updateSelf(id,name,parentId,typeId,scopeIdList);
        return Result.success(true);
    }

    @ApiOperation(value = "删除",tags = "删除")
    @PostMapping("delete")
    public Result<Boolean> delete(@RequestParam Integer id){
        downBoxNameService.removeBySelf(id);
        return Result.success(true);
    }

    @ApiOperation(value = "查询所有下拉框名字以及下拉框数据",tags = "查询")
    @PostMapping("list")
    public Result<List<DownBoxNameVO>> list(){
        List<DownBoxNameVO> result = downBoxNameService.listAndData();
        return Result.success(result);
    }

}

