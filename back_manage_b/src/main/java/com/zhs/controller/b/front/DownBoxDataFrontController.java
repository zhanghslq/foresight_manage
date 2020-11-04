package com.zhs.controller.b.front;


import com.zhs.common.Result;
import com.zhs.entity.DownBoxData;
import com.zhs.model.bo.DownBoxDataBO;
import com.zhs.service.DownBoxDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-09-28
 */
@Api(tags = "下拉框实际数据")
@RestController
@RequestMapping("/down_box_data_front")
public class DownBoxDataFrontController {

    @Resource
    private DownBoxDataService downBoxDataService;


    @PostMapping("list/tree/by_type_and_scope")
    @ApiOperation(value = "根据类型和作用域获取下拉框数据（树状结构）",tags = "查询")
    public Result<List<DownBoxDataBO>> listTreeByDownBoxTypeAndScope(@RequestParam Integer downBoxTypeId, @RequestParam Integer scopeId){
        List<DownBoxDataBO> list = downBoxDataService.listByDownBoxTypeAndScope(downBoxTypeId,scopeId);
        return Result.success(list);
    }

    @ApiOperation(value = "平铺的下拉框数据",tags = "查询")
    @PostMapping("list/no_tree/by_type_andScope")
    public Result<List<DownBoxData>> listNoTreeByDownBoxTypeAndScope(@RequestParam Integer downBoxTypeId, @RequestParam Integer scopeId){
        List<DownBoxData> list = downBoxDataService.listNoTreeByDownBoxTypeAndScope(downBoxTypeId,scopeId);
        return Result.success(list);
    }
}

