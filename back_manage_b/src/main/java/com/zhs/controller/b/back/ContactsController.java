package com.zhs.controller.b.back;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.DropDownBoxTypeEnum;
import com.zhs.common.constant.ScopeEnum;
import com.zhs.entity.CommonData;
import com.zhs.entity.Contacts;
import com.zhs.entity.DownBoxData;
import com.zhs.model.vo.ContactsVO;
import com.zhs.service.CommonDataService;
import com.zhs.service.ContactsService;
import com.zhs.service.DownBoxDataService;
import com.zhs.util.EasyExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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
    private DownBoxDataService downBoxDataService;


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


        List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), null);


        Map<Integer, String> map = downBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, DownBoxData::getName));
        Page<ContactsVO> contactsVOPage = new Page<>();
        BeanUtil.copyProperties(page,contactsVOPage);
        List<Contacts> records = page.getRecords();
        List<ContactsVO> contactsVOS = new ArrayList<>();
        for (Contacts record : records) {
            ContactsVO contactsVO = new ContactsVO();
            BeanUtil.copyProperties(record,contactsVO);
            if(!Objects.isNull(record.getLevelId())){
                contactsVO.setLevelName(map.get(record.getLevelId().intValue()));
            }
            contactsVOS.add(contactsVO);
        }
        contactsVOPage.setRecords(contactsVOS);
        return Result.success(contactsVOPage);
    }

    @PostMapping("search/list")
    @ApiOperation(value = "联系人列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
            @ApiImplicitParam(name = "updateTimeBegin",value = "更新开始时间"),
            @ApiImplicitParam(name = "updateTimeEnd",value = "更新结束时间"),
    })
    @ApiOperationSupport(ignoreParameters = {"deleted"})
    public Result<Page<ContactsVO>> searchListByPage(Contacts contacts, Date updateTimeBegin, Date updateTimeEnd, @RequestParam Integer current, @RequestParam Integer size){
        Page<Contacts> contactsPage = new Page<>(current, size);
        QueryWrapper<Contacts> contactsQueryWrapper = new QueryWrapper<>();

        // 联系人编号
        contactsQueryWrapper.eq(!StringUtils.isEmpty(contacts.getId()),"id",contacts.getId());
        // 姓名
        contactsQueryWrapper.like(!StringUtils.isEmpty(contacts.getRealName()),"real_name",contacts.getRealName());

        //单位
        contactsQueryWrapper.like(!StringUtils.isEmpty(contacts.getCompanyName()),"company_name",contacts.getCompanyName());
        //部门
        contactsQueryWrapper.like(!StringUtils.isEmpty(contacts.getDepartmentName()),"department_name",contacts.getDepartmentName());

        //职务
        contactsQueryWrapper.like(!StringUtils.isEmpty(contacts.getJob()),"job",contacts.getJob());

        //联系电话
        contactsQueryWrapper.like(!StringUtils.isEmpty(contacts.getConcatPhone()),"concat_phone",contacts.getConcatPhone());
        // 行政级别
        contactsQueryWrapper.like(!StringUtils.isEmpty(contacts.getLevelName()),"level_name",contacts.getLevelName());
        contactsQueryWrapper.eq(!StringUtils.isEmpty(contacts.getLevelId()),"level_id",contacts.getLevelId());
        //更新时间
        if(!Objects.isNull(updateTimeBegin)&&!Objects.isNull(updateTimeEnd)){
            contactsQueryWrapper.ge("update_time", updateTimeBegin);
            contactsQueryWrapper.le("update_time",updateTimeEnd);
        }
        //渠道
        Page<Contacts> page = contactsService.page(contactsPage,contactsQueryWrapper);

        // 领导人行政级别
        List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), null);
        Map<Integer, String> map = downBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, DownBoxData::getName));
        Page<ContactsVO> contactsVOPage = new Page<>();
        BeanUtil.copyProperties(page,contactsVOPage);
        List<Contacts> records = page.getRecords();
        List<ContactsVO> contactsVOS = new ArrayList<>();
        for (Contacts record : records) {
            ContactsVO contactsVO = new ContactsVO();
            BeanUtil.copyProperties(record,contactsVO);
            contactsVO.setLevelName(map.get(record.getLevelId().intValue()));
            contactsVOS.add(contactsVO);
        }
        contactsVOPage.setRecords(contactsVOS);
        return Result.success(contactsVOPage);
    }

    @PostMapping("update")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    @ApiOperation(value = "修改联系人",tags = "修改")
    public Result<Boolean> update(@RequestBody Contacts contacts){
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
    @ApiOperation(value = "插入联系人",tags = "新增")
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
    @ApiOperation(value = "删除联系人",tags = "删除")
    @ApiImplicitParam(name = "id",value = "联系人id",required = true)
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(contactsService.removeById(id));
    }
    // 插入联系人用的是导入文件模板的形式

    @ApiOperation(value = "批量删除联系人",tags = "删除")
    @PostMapping("delete/by_ids")
    @ApiImplicitParam(name = "ids",value = "多个逗号相隔",required = true)
    public Result<Boolean> deleteByIds(@RequestParam List<Long> ids){
        return Result.success(contactsService.removeByIds(ids));
    }

    @ApiOperation(value = "绑定联系人",tags = "修改")
    @PostMapping("binding/organization")
    public Result<Boolean> bindOrganization(@RequestParam Integer isCompany,@RequestParam Long organizationId,@RequestParam(required = false) List<Long> contactIds){
        contactsService.bindingOrganization(isCompany,organizationId,contactIds);
        return Result.success(true);
    }

    @ApiOperation(value = "上传文件进行批量插入联系人",tags = "新增")
    @PostMapping("listUpload")
    public Result<String> listUpload(@RequestParam("file") MultipartFile file){
        //批量添加
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)){
            return Result.fail(500,"文件不能为空","");
        }
        // 获取文件后缀
        String prefix=fileName.substring(fileName.lastIndexOf("."));
        if (!prefix.toLowerCase().contains("xls") && !prefix.toLowerCase().contains("xlsx") ){
            return Result.fail(500,"文件格式异常，请上传Excel文件格式","");
        }
        // 防止生成的临时文件重复-建议使用UUID
        final File excelFile;
        try {
            excelFile = File.createTempFile(System.currentTimeMillis()+"", prefix);
            file.transferTo(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail(500,"文件上传失败","");
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(excelFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return Result.fail(500,"文件上传失败","");
        }
        List<Contacts> readBooks = EasyExcelUtil.readListFrom(fileInputStream, Contacts.class);
        excelFile.delete();
        contactsService.saveBatchSelf(readBooks);
        return Result.success("");
    }
}

