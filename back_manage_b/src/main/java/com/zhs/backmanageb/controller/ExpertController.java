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
import com.zhs.backmanageb.model.vo.ExpertVO;
import com.zhs.backmanageb.service.CommonDataService;
import com.zhs.backmanageb.service.ExpertService;
import com.zhs.backmanageb.util.EasyExcelUtil;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 专家 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "专家管理")
@RestController
@RequestMapping("/expert")
public class ExpertController {
    @Resource
    private ExpertService expertService;
    @Resource
    private CommonDataService commonDataService;

    @ApiOperation(value = "获取专家列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    @PostMapping("list")
    public Result<Page<ExpertVO>> list(@RequestParam Integer current,@RequestParam Integer size){
        Page<Expert> expertPage = new Page<>(current, size);
        Page<Expert> page = expertService.page(expertPage);
        // 领导人行政级别
        QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
        commonDataQueryWrapper.eq("type", DropDownBoxTypeEnum.EXPERT_LEVEL.getId());
        List<CommonData> commonDataList = commonDataService.list(commonDataQueryWrapper);
        Map<Long, String> map = commonDataList.stream().collect(Collectors.toMap(CommonData::getId, CommonData::getName));
        Page<ExpertVO> expertVOPage = new Page<>();
        BeanUtil.copyProperties(page,expertVOPage);
        List<Expert> records = page.getRecords();
        List<ExpertVO> expertVOS = new ArrayList<>();
        for (Expert record : records) {
            ExpertVO expertVO = new ExpertVO();
            BeanUtil.copyProperties(record,expertVO);
            expertVO.setLevelName(map.get(record.getLevelId()));
            expertVOS.add(expertVO);
        }
        expertVOPage.setRecords(expertVOS);
        return Result.success(expertVOPage);
    }

    @PostMapping("query")
    @ApiOperation(value = "获取详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<Expert> queryById(@RequestParam Long id){
        Expert expert = expertService.getById(id);
        return Result.success(expert);
    }
    @PostMapping("update")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    @ApiOperation(value = "更新方法",tags = "修改")
    public Result<Boolean> update(@RequestBody Expert expert){
        return Result.success(expertService.updateById(expert));
    }

    @ApiOperation(value = "根据id删除",tags = "删除")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    @PostMapping("delete")
    public Result<Boolean> deleteById(@RequestParam Long id){
        return Result.success(expertService.removeById(id));
    }
    @PostMapping("insert")
    @ApiOperation(value = "插入",tags = "新增")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(@RequestBody Expert expert){
        // 插入的时候需要记录操作人id
        try {
            Object principal = SecurityUtils.getSubject().getPrincipal();
            Long adminId = Long.valueOf(principal.toString());
            expert.setAdminId(adminId);
        } catch (NumberFormatException e) {
            expert.setAdminId(0L);
        }

        return Result.success(expertService.save(expert));
    }
    @ApiOperation(value = "批量删除",tags = "删除")
    @PostMapping("delete/by_ids")
    @ApiImplicitParam(name = "ids",value = "多个逗号相隔",required = true)
    public Result<Boolean> deleteByIds(@RequestParam List<Long> ids){
        return Result.success(expertService.removeByIds(ids));
    }
    @ApiOperation(value = "上传文件进行批量插入",tags = "新增")
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
        List<Expert> readBooks = EasyExcelUtil.readListFrom(fileInputStream, Expert.class);
        excelFile.delete();
        expertService.saveBatchSelf(readBooks);
        return Result.success("");
    }
}

