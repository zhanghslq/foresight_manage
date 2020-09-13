package com.zhs.controller.b.back;


import cn.hutool.core.bean.BeanUtil;
import com.zhs.common.Result;
import com.zhs.common.constant.DropDownBoxTypeEnum;
import com.zhs.entity.DropDownBoxType;
import com.zhs.exception.MyException;
import com.zhs.model.vo.DropDownBoxTypeVO;
import com.zhs.service.DropDownBoxTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 下拉框的类型 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-09-07
 */
@RestController
@RequestMapping("/dropDownBoxType")
@Api(tags = "下拉框")
public class DropDownBoxTypeController {
    @Resource
    private DropDownBoxTypeService dropDownBoxTypeService;


    @PostMapping("/add")
    @ApiOperation(value = "创建下拉框",tags = "新增")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id",value = "下拉框id"),
            @ApiImplicitParam(name = "name",value = "名称，会显示到系统配置")
    })
    public Result<Boolean> add(@RequestParam Integer id, @RequestParam String name){
        // 先查是否存在
        DropDownBoxType byId = dropDownBoxTypeService.getById(id);
        if(!Objects.isNull(byId)){
            throw new MyException("下拉框已存在");
        }
        DropDownBoxType dropDownBoxType = new DropDownBoxType();
        dropDownBoxType.setId(id);
        dropDownBoxType.setName(name);
        dropDownBoxTypeService.save(dropDownBoxType);
        return Result.success(true);
    }
    @PostMapping("/list")
    @ApiOperation(value = "下拉框添加的时候二级联动的所有数据，可以根据typeId分类",tags = "查询")
    public Result<List<DropDownBoxTypeVO>> list(){
        DropDownBoxTypeEnum[] values = DropDownBoxTypeEnum.values();

        List<DropDownBoxTypeVO> result = new ArrayList<>();
        for (DropDownBoxTypeEnum value : values) {
            DropDownBoxTypeVO dropDownBoxTypeVO = new DropDownBoxTypeVO();
            BeanUtil.copyProperties(value,dropDownBoxTypeVO);
            result.add(dropDownBoxTypeVO);
        }
        return Result.success(result);
    }



}

