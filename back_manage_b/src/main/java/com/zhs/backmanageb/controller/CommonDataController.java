package com.zhs.backmanageb.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.common.constant.DropDownBoxTypeEnum;
import com.zhs.backmanageb.entity.CommonData;
import com.zhs.backmanageb.model.vo.CommonTypeVO;
import com.zhs.backmanageb.service.CommonDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
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
 * 一些公用数据（下拉框选择的） 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "系统公共配置，（下拉框取值）")
@RestController
@RequestMapping("/commonData")
public class CommonDataController {
    // 这个维护的值只允许修改不允许删除

    @Resource
    private CommonDataService commonDataService;

    @ApiOperation("根据类别获取列表进行维护,用于下拉框取值，也可以用于页面管理，不允许删除")
    @ApiImplicitParam(name="type",value = "类型",required = true)
    @PostMapping("/list/by_type")
    public Result<List<CommonData>> listByType(@RequestParam Integer type){
        QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
        commonDataQueryWrapper.eq("type",type);
        List<CommonData> list = commonDataService.list(commonDataQueryWrapper);
        return Result.success(list);
    }
    @PostMapping("/list/all_type")
    @ApiOperation("所有维护的下拉框列表，以及对应的id就是type,用于人工看，系统暂时不会用到，可直接写死，有变动会通知")
    public Result<List<CommonTypeVO>> listAllType(){
        List<CommonTypeVO> result = new ArrayList<>();
        List<CommonData> list = commonDataService.list();
        Map<Integer, List<CommonData>> map = list.stream().collect(Collectors.groupingBy(CommonData::getType));
        DropDownBoxTypeEnum[] values = DropDownBoxTypeEnum.values();
        for (DropDownBoxTypeEnum value : values) {
            CommonTypeVO commonTypeVO = new CommonTypeVO();
            commonTypeVO.setId(Long.valueOf(value.getId()));
            commonTypeVO.setName(value.getName());
            List<CommonData> commonData = map.get(value.getId());
            commonTypeVO.setChildren(commonData);
            result.add(commonTypeVO);
        }
        return Result.success(result);
    }
    @ApiOperation("插入下拉框数据，id，time，deleted等值不需要考虑，不用传，传name,type就行，其他接口类似")
    @PostMapping("insert")
    @ApiOperationSupport(ignoreParameters = {"id","deleted","createTime","updateTime"})
    public Result<Boolean> insert(CommonData commonData){
        Assert.notNull(commonData.getName(),"名字不允许为空");
        Assert.notNull(commonData.getType(),"类型不允许为空");

        return Result.success(commonDataService.save(commonData));
    }
    @PostMapping("insertBatch")
    @ApiOperation("批量添加")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "content",value = "文本域内容",required = true),
            @ApiImplicitParam(name = "type",value = "类型",required = true),
    })
    public Result<Boolean> insertBatch(@RequestParam String content,@RequestParam Integer type){
        commonDataService.insertBatch(content,type);
        return Result.success(true);
    }

    @ApiOperation("修改")
    @PostMapping("update")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    public Result<Boolean> update(CommonData commonData){
        return Result.success(commonDataService.updateById(commonData));
    }
}

