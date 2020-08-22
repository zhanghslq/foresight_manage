package com.zhs.backmanageb.controller;


import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.service.ResumeCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 简历对应的多个单位，多个职务问题，跟企业表无关系 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-08-17
 */
@RestController
@RequestMapping("/resumeCompany")
@Api(tags = "简历关联当前单位")
public class ResumeCompanyController {

    @Resource
    private ResumeCompanyService resumeCompanyService;

    @ApiOperation(value = "删除简历关联当前单位",tags = "删除")
    @PostMapping("delete")
    public Result<Boolean> delete(@RequestParam Long id){
        return Result.success(resumeCompanyService.removeById(id));
    }
}

