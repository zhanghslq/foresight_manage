package com.zhs.backmanageb.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.common.constant.DropDownBoxTypeEnum;
import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.entity.Expert;
import com.zhs.backmanageb.model.vo.ContactsVO;
import com.zhs.backmanageb.model.vo.ExpertVO;
import com.zhs.backmanageb.service.CommonDataService;
import com.zhs.backmanageb.service.ContactsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Resource
    private CommonDataService commonDataService;

    @PostMapping("list")
    @ApiOperation(value = "联系人列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    public Result<Page<ContactsVO>> listByPage(@RequestParam Integer current, @RequestParam Integer size){
        Page<Contacts> contactsPage = new Page<>(current, size);
        Page<Contacts> page = contactsService.page(contactsPage);
        // 领导人行政级别
        QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
        commonDataQueryWrapper.eq("type", DropDownBoxTypeEnum.CONCAT_LEVEL.getId());
        List<CommonData> commonDataList = commonDataService.list(commonDataQueryWrapper);
        Map<Long, String> map = commonDataList.stream().collect(Collectors.toMap(CommonData::getId, CommonData::getName));
        Page<ContactsVO> contactsVOPage = new Page<>();
        BeanUtil.copyProperties(page,contactsVOPage);
        List<Contacts> records = page.getRecords();
        List<ContactsVO> contactsVOS = new ArrayList<>();
        for (Contacts record : records) {
            ContactsVO contactsVO = new ContactsVO();
            BeanUtil.copyProperties(record,contactsVO);
            contactsVO.setLevelName(map.get(record.getLevelId()));
            contactsVOS.add(contactsVO);
        }
        contactsVOPage.setRecords(contactsVOS);
        return Result.success(contactsVOPage);
    }

    @PostMapping("update")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    @ApiOperation(value = "修改联系人",tags = "修改")
    public Result<Boolean> update(Contacts contacts){
        // 这样更改会显得属性有点多，后面可以针对性的进行精简
        return Result.success(contactsService.updateById(contacts));
    }

    @ApiOperation(value = "获取联系人详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "id",required = true)
    @PostMapping("query")
    public Result<Contacts> queryById(@RequestParam Long id){
        Contacts contacts = contactsService.getById(id);
        return Result.success(contacts);
    }
    @PostMapping("insert")
    @ApiOperation(value = "插入",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(Contacts contacts){
        // 插入的时候需要记录操作人id
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            Long adminId = Long.valueOf(principal.toString());
            contacts.setAdminId(adminId);
        } catch (NumberFormatException e) {
            contacts.setAdminId(0L);
        }
        return Result.success(contactsService.save(contacts));
    }


    @PostMapping("delete")
    @ApiOperation(value = "删除",tags = "删除")
    @ApiImplicitParam(name = "id",value = "联系人id",required = true)
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(contactsService.removeById(id));
    }
    // 插入联系人用的是导入文件模板的形式

    @ApiOperation(value = "批量删除",tags = "删除")
    @PostMapping("delete/by_ids")
    @ApiImplicitParam(name = "ids",value = "多个逗号相隔",required = true)
    public Result<Boolean> deleteByIds(@RequestParam List<Long> ids){
        return Result.success(contactsService.removeByIds(ids));
    }
}

