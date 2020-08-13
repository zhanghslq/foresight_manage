package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.entity.ModuleContacts;
import com.zhs.backmanageb.service.ContactsService;
import com.zhs.backmanageb.service.ModuleContactsService;
import io.swagger.annotations.*;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 模块-联系人关系表 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-08-11
 */
@Api(tags = "组织模块-联系人")
@RestController
@RequestMapping("/moduleContacts")
public class ModuleContactsController {

    @Resource
    private ModuleContactsService moduleContactsService;
    @Resource
    private ContactsService contactsService;

    @ApiOperation(value = "绑定联系人",tags = "修改")
    @PostMapping("binding/contacts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId",value = "模块id",required = true),
            @ApiImplicitParam(name = "contactIds",value = "联系人id，多个逗号隔开",required = true)
    })
    public Result<Boolean> bindingContacts(@RequestParam Long moduleId, @RequestParam List<Long> contactIds){
        Assert.notEmpty(contactIds,"联系人id不能为空");
        ArrayList<ModuleContacts> moduleContactsArrayList = new ArrayList<>();
        ArrayList<Contacts> contactsArrayList = new ArrayList<>();

        for (Long contactId : contactIds) {
            ModuleContacts moduleContacts = new ModuleContacts();
            moduleContacts.setContactId(contactId);
            moduleContacts.setModuleId(moduleId);
            moduleContactsArrayList.add(moduleContacts);

            Contacts contacts = new Contacts();
            contacts.setId(contactId);
            contacts.setAssociatedWithOrganization(1);
            contactsArrayList.add(contacts);
        }
        moduleContactsService.saveBatch(moduleContactsArrayList);
        // 绑定之后设置为已关联组织机构
        contactsService.updateBatchById(contactsArrayList);
        return Result.success(true);
    }
    @ApiOperation(value = "取消绑定联系人",tags = "修改")
    @PostMapping("cancel/binding/contacts")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId",value = "模块id",required = true),
            @ApiImplicitParam(name = "contactIds",value = "联系人id，多个逗号隔开",required = true)
    })
    public Result<Boolean> cancelBindingContacts(@RequestParam Long moduleId, @RequestParam List<Long> contactIds){
        Assert.notEmpty(contactIds,"联系人id不能为空");
        QueryWrapper<ModuleContacts> moduleContactsQueryWrapper = new QueryWrapper<>();
        moduleContactsQueryWrapper.eq("module_id",moduleId);
        moduleContactsQueryWrapper.in("contact_id",contactIds);
        moduleContactsService.remove(moduleContactsQueryWrapper);
        // 取消绑定需要看是否还关联有组织
        QueryWrapper<ModuleContacts> moduleQueryWrapper = new QueryWrapper<>();
        moduleQueryWrapper.in("contact_id",contactIds);
        List<ModuleContacts> moduleContacts = moduleContactsService.list(moduleQueryWrapper);
        List<Long> contactIdList = moduleContacts.stream().map(ModuleContacts::getContactId).collect(Collectors.toList());
        contactIds.removeAll(contactIdList);
        if(contactIds.size()>0){
            ArrayList<Contacts> contactsArrayList = new ArrayList<>();
            for (Long contactId : contactIds) {
                Contacts contacts = new Contacts();
                contacts.setId(contactId);
                contacts.setAssociatedWithOrganization(0);
                contactsArrayList.add(contacts);
            }
            contactsService.updateBatchById(contactsArrayList);
        }
        return Result.success(true);
    }

}

