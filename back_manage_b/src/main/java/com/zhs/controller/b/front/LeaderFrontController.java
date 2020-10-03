package com.zhs.controller.b.front;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.Leader;
import com.zhs.service.LeaderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 领导人 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Api(tags = "领导管理")
@RestController
@RequestMapping("/leader_front")
public class LeaderFrontController {
    @Resource
    private LeaderService leaderService;

    @PostMapping("queryById")
    @ApiOperation(value = "查询领导详情",tags = "查询")
    @ApiImplicitParam(name = "id",value = "编号",required = true)
    public Result<Leader> queryById(Long id){
        return Result.success(leaderService.getById(id));
    }
    @PostMapping("organizationId")
    @ApiOperation(value = "根据组织id查询领导",tags = "查询")
    public Result<List<Leader>> listByOrganizationId(@RequestParam Long organizationId){
        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("organization_id",organizationId);
        List<Leader> list = leaderService.list(leaderQueryWrapper);
        return Result.success(list);
    }



}

