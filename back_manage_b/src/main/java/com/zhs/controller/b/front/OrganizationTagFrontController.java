package com.zhs.controller.b.front;


import com.zhs.common.Result;
import com.zhs.entity.OrganizationTag;
import com.zhs.service.OrganizationTagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 组织标签表 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-30
 */
@Api(tags = "组织标签管理")
@RestController
@RequestMapping("/organizationTag_front")
public class OrganizationTagFrontController {

    @Resource
    private OrganizationTagService organizationTagService;

    @ApiOperation(value = "查询所有的标签",tags = "查询")
    @PostMapping("list")
    public Result<Object> listTags(){
        List<OrganizationTag> list = organizationTagService.list();
        return Result.success(list);
    }
}

