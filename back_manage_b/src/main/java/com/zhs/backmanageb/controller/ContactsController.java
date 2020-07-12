package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.service.ContactsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 联系人 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@RestController
@RequestMapping("/contacts")
@Api(tags = "联系人管理")
public class ContactsController {
    @Resource
    private ContactsService contactsService;

    @PostMapping("list")
    @ApiOperation("联系人列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    public Result<Page<Contacts>> listByPage(@RequestParam Integer current, @RequestParam Integer size){
        Page<Contacts> contactsPage = new Page<>(current, size);
        Page<Contacts> page1 = contactsService.page(contactsPage);
        return Result.success(page1);
    }

    @PostMapping("update")
    @ApiOperation("修改联系人")
    public Result<Boolean> update(Contacts contacts){
        // 这样更改会显得属性有点多，后面可以针对性的进行精简
        return Result.success(contactsService.updateById(contacts));
    }

    @ApiOperation("获取联系人详情")
    @ApiImplicitParam(name = "id",value = "id",required = true)
    @PostMapping("query")
    public Result<Contacts> queryById(@RequestParam Long id){
        Contacts contacts = contactsService.getById(id);
        return Result.success(contacts);
    }


    // 插入联系人用的是导入文件模板的形式
}

