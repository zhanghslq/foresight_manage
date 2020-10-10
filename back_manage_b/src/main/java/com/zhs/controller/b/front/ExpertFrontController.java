package com.zhs.controller.b.front;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.entity.DownBoxData;
import com.zhs.entity.Expert;
import com.zhs.model.vo.ExpertVO;
import com.zhs.model.vo.InputStatisticsVO;
import com.zhs.service.DownBoxDataService;
import com.zhs.service.ExpertService;
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
 * 专家 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "专家管理")
@RestController
@RequestMapping("/expert_front")
public class ExpertFrontController {
    @Resource
    private ExpertService expertService;


    @Resource
    private DownBoxDataService downBoxDataService;

    @ApiOperation(value = "获取专家列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
    })
    @PostMapping("list")
    public Result<Page<ExpertVO>> list(@RequestParam Integer current, @RequestParam Integer size){
        Page<Expert> expertPage = new Page<>(current, size);
        Page<Expert> page = expertService.page(expertPage);
        List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.EXPERT_LEVEL.getId(), null);
        Map<Integer, String> map = downBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, DownBoxData::getName));
        Page<ExpertVO> expertVOPage = new Page<>();
        BeanUtil.copyProperties(page,expertVOPage);
        List<Expert> records = page.getRecords();
        List<ExpertVO> expertVOS = new ArrayList<>();
        for (Expert record : records) {
            ExpertVO expertVO = new ExpertVO();
            BeanUtil.copyProperties(record,expertVO);
            expertVO.setLevelName(map.get(record.getLevelId().intValue()));
            expertVOS.add(expertVO);
        }
        expertVOPage.setRecords(expertVOS);
        return Result.success(expertVOPage);
    }

    @ApiOperation(value = "根据专家类别获取专家列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
            @ApiImplicitParam(name = "classificationId",value = "专家类别id",required = true),
    })
    @PostMapping("list/by_classificationId")
    public Result<Page<ExpertVO>> listByClassification(@RequestParam Long classificationId, @RequestParam Integer current,@RequestParam Integer size){
        Page<Expert> expertPage = new Page<>(current, size);
        QueryWrapper<Expert> expertQueryWrapper = new QueryWrapper<>();
        expertQueryWrapper.eq("classification_id",classificationId);
        Page<Expert> page = expertService.page(expertPage,expertQueryWrapper);
        // 领导人行政级别
        List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.EXPERT_LEVEL.getId(), null);
        Map<Integer, String> map = downBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, DownBoxData::getName));
        Page<ExpertVO> expertVOPage = new Page<>();
        BeanUtil.copyProperties(page,expertVOPage);
        List<Expert> records = page.getRecords();
        List<ExpertVO> expertVOS = new ArrayList<>();
        for (Expert record : records) {
            ExpertVO expertVO = new ExpertVO();
            BeanUtil.copyProperties(record,expertVO);
            if(!Objects.isNull(record.getLevelId())){
                expertVO.setLevelName(map.get(record.getLevelId().intValue()));
            }
            expertVOS.add(expertVO);
        }
        expertVOPage.setRecords(expertVOS);
        return Result.success(expertVOPage);
    }
    @ApiOperation(value = "根据条件获取专家列表",tags = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current",value = "当前页",required = true),
            @ApiImplicitParam(name = "size",value = "每页多少条",required = true),
            @ApiImplicitParam(name = "updateTimeBegin",value = "更新开始时间"),
            @ApiImplicitParam(name = "updateTimeEnd",value = "更新结束时间"),
    })
    @PostMapping("search/list")
    @ApiOperationSupport(ignoreParameters = {"deleted"})
    public Result<Page<ExpertVO>> searchList(Expert expert, Date updateTimeBegin,Date updateTimeEnd, @RequestParam Integer current, @RequestParam Integer size){
        Page<Expert> expertPage = new Page<>(current, size);
        QueryWrapper<Expert> expertQueryWrapper = new QueryWrapper<>();
        // 专家编号
        expertQueryWrapper.eq(!StringUtils.isEmpty(expert.getId()),"id",expert.getId());
        // 专家类别
        expertQueryWrapper.eq(!StringUtils.isEmpty(expert.getClassificationId()),"classification_id",expert.getClassificationId());
        // 姓名
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getRealName()),"real_name",expert.getRealName());
        // 单位
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getCompany()),"company", expert.getCompany());
        // 部门
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getDepartment()),"department",expert.getDepartment());
        //职务
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getJob()),"job",expert.getJob());
        //联系电话
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getMobile()),"mobile",expert.getMobile());
        //级别
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getLevelId()),"level_id",expert.getLevelId());
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getLevelName()),"level_name",expert.getLevelName());
        // 更新时间
        if(!Objects.isNull(updateTimeBegin)&&!Objects.isNull(updateTimeEnd)){
            expertQueryWrapper.ge("update_time", updateTimeBegin);
            expertQueryWrapper.le("update_time",updateTimeEnd);
        }
        //从事领域
        expertQueryWrapper.like(!StringUtils.isEmpty(expert.getWorkArea()),"work_area",expert.getWorkArea());
        expertQueryWrapper.eq(!StringUtils.isEmpty(expert.getWorkAreaId()),"work_area_id",expert.getWorkAreaId());
        Page<Expert> page = expertService.page(expertPage,expertQueryWrapper);
        // 领导人行政级别

        List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.EXPERT_LEVEL.getId(), null);
        Map<Integer, String> map = downBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, DownBoxData::getName));
        Page<ExpertVO> expertVOPage = new Page<>();
        BeanUtil.copyProperties(page,expertVOPage);
        List<Expert> records = page.getRecords();
        List<ExpertVO> expertVOS = new ArrayList<>();
        for (Expert record : records) {
            ExpertVO expertVO = new ExpertVO();
            BeanUtil.copyProperties(record,expertVO);
            expertVO.setLevelName(map.get(record.getLevelId().intValue()));
            expertVOS.add(expertVO);
        }
        expertVOPage.setRecords(expertVOS);
        return Result.success(expertVOPage);
    }
    @PostMapping("query")
    @ApiOperation(value = "获取专家详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<Expert> queryById(@RequestParam Long id){
        Expert expert = expertService.getById(id);
        return Result.success(expert);
    }

    @ApiOperation(value = "专家录入统计",tags = "查询")
    @PostMapping("expert_input/statistics")
    public Result<List<InputStatisticsVO>> expertInputStatistics(){
        List<InputStatisticsVO> inputStatisticsVO = expertService.expertInputStatistics();
        return Result.success(inputStatisticsVO);
    }
}

