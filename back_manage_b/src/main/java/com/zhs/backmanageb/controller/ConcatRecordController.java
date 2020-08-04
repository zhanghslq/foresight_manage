package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.ConcatRecord;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.model.vo.ConcatRecordVO;
import com.zhs.backmanageb.service.ConcatRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 联系记录表 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "联系记录管理")
@RestController
@RequestMapping("/concatRecord")
public class ConcatRecordController {
    @Resource
    private ConcatRecordService concatRecordService;

    @PostMapping("list")
    @ApiOperation(value = "联系记录列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    public Result<Page<ConcatRecord>> listByPage(@RequestParam Integer current, @RequestParam Integer size){
        Page<ConcatRecord> contactsPage = new Page<>(current, size);
        Page<ConcatRecord> page1 = concatRecordService.page(contactsPage);
        return Result.success(page1);
    }
    @PostMapping("delete")
    @ApiOperation(value = "根据id删除联系记录",tags = "删除")
    @ApiImplicitParam(name = "id",value = "联系记录id",required = true)
    public Result<Boolean> deleteById(@RequestParam Long id){
        return Result.success(concatRecordService.removeById(id));
    }

    @PostMapping("query")
    @ApiOperation(value = "根据id获取联系记录详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "联系记录id",required = true)
    public Result<ConcatRecordVO> queryById(@RequestParam Long id){
        //列表和详情应该需要经过稍微处理，把id替换掉，变成人的姓名
        return Result.success(concatRecordService.queryDetail(id));
    }

    @PostMapping("update")
    @ApiOperation(value = "修改联系记录",tags = "修改")
    public Result<Boolean> update(ConcatRecord concatRecord){
        return Result.success(concatRecordService.updateById(concatRecord));
    }
    @PostMapping("insert")
    @ApiOperation(value = "插入联系记录",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(ConcatRecord concatRecord){
        concatRecordService.save(concatRecord);
        return Result.success(true);
    }
    @ApiOperation(value = "批量删除",tags = "删除")
    @PostMapping("delete/by_ids")
    @ApiImplicitParam(name = "ids",value = "多个逗号相隔",required = true)
    public Result<Boolean> deleteByIds(@RequestParam List<Long> ids){
        return Result.success(concatRecordService.removeByIds(ids));
    }
}

