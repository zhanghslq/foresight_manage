package com.zhs.backmanageb.controller;


import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.entity.ConcatRecord;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.exception.MyException;
import com.zhs.backmanageb.model.vo.ConcatRecordVO;
import com.zhs.backmanageb.service.ConcatRecordService;
import com.zhs.backmanageb.util.EasyExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

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
    @PostMapping("search/list")
    @ApiOperation(value = "根据条件联系记录列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    @ApiOperationSupport(ignoreParameters = {"deleted"})
    public Result<Page<ConcatRecord>> searchListByPage(ConcatRecord concatRecord, @RequestParam Integer current, @RequestParam Integer size){
        QueryWrapper<ConcatRecord> concatRecordQueryWrapper = new QueryWrapper<>();
        // 编号
        concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getId()),"id",concatRecord.getId());
        // 发布时间 // 需要得到开始时间结束时间，然后用大小进行判断
        if(!Objects.isNull(concatRecord.getCreateTime())){
            concatRecordQueryWrapper.ge("create_time", DateUtil.beginOfDay(concatRecord.getCreateTime()));
            concatRecordQueryWrapper.le("create_time",DateUtil.endOfDay(concatRecord.getCreateTime()));
        }
        // 联络人
        concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getOperatorName()),"operator_name",concatRecord.getOperatorName());
        //姓名
        concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getConcatPersonName()),"concat_person_name",concatRecord.getConcatPersonName());
        //单位
        concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getCompanyName()),"company_name",concatRecord.getCompanyName());
        //职务
        concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getJob()),"job",concatRecord.getJob());
        //沟通类型
        concatRecordQueryWrapper.eq(!Objects.isNull(concatRecord.getConcatType()),"concat_type",concatRecord.getConcatType());
        //沟通频率
        concatRecordQueryWrapper.eq(!Objects.isNull(concatRecord.getConcatCount()),"concat_count",concatRecord.getConcatCount());
        Page<ConcatRecord> contactsPage = new Page<>(current, size);
        Page<ConcatRecord> page1 = concatRecordService.page(contactsPage,concatRecordQueryWrapper);
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

    @PostMapping(value = "export/excel_by_condition")
    @ApiOperation(value = "导出联系记录",tags = "查询",produces="application/octet-stream")
    public void exportExcel(HttpServletResponse response,@RequestParam(required = false) List<Long> recordIds,ConcatRecord concatRecord){
        List<ConcatRecord> list;
        if(Objects.isNull(recordIds)||recordIds.size()==0){
            QueryWrapper<ConcatRecord> concatRecordQueryWrapper = new QueryWrapper<>();
            // 编号
            concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getId()),"id",concatRecord.getId());
            // 发布时间 // 需要得到开始时间结束时间，然后用大小进行判断
            if(!Objects.isNull(concatRecord.getCreateTime())){
                concatRecordQueryWrapper.ge("create_time", DateUtil.beginOfDay(concatRecord.getCreateTime()));
                concatRecordQueryWrapper.le("create_time",DateUtil.endOfDay(concatRecord.getCreateTime()));
            }
            // 联络人
            concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getOperatorName()),"operator_name",concatRecord.getOperatorName());
            //姓名
            concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getConcatPersonName()),"concat_person_name",concatRecord.getConcatPersonName());
            //单位
            concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getCompanyName()),"company_name",concatRecord.getCompanyName());
            //职务
            concatRecordQueryWrapper.like(!Objects.isNull(concatRecord.getJob()),"job",concatRecord.getJob());
            //沟通类型
            concatRecordQueryWrapper.eq(!Objects.isNull(concatRecord.getConcatType()),"concat_type",concatRecord.getConcatType());
            //沟通频率
            concatRecordQueryWrapper.eq(!Objects.isNull(concatRecord.getConcatCount()),"concat_count",concatRecord.getConcatCount());
            list = concatRecordService.list(concatRecordQueryWrapper);
        }else {
            list = concatRecordService.listByIds(recordIds);
        }
        // 导出
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename="
                + new String(("联络记录"+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))).getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1)
                + ".xlsx");
        ServletOutputStream outputStream;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            throw new MyException("导出出错，稍后重试");
        }
        EasyExcelUtil.writeListTo(outputStream,list,ConcatRecord.class);
    }
}

