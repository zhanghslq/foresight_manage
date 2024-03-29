package com.zhs.controller.b.back;


import cn.hutool.core.util.StrUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.zhs.common.Result;
import com.zhs.entity.AdminOperationLog;
import com.zhs.entity.OrganizationModule;
import com.zhs.model.dto.OrganizationModuleSeqDTO;
import com.zhs.service.AdminOperationLogService;
import com.zhs.service.OrganizationModuleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 组织模块 前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-07-27
 */
@Api(tags = "组织模块")
@RestController
@RequestMapping("/organizationModule")
public class OrganizationModuleController {
    @Resource
    private AdminOperationLogService adminOperationLogService;

    @Resource
    private OrganizationModuleService organizationModuleService;

    @PostMapping("insert")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "organizationId" ,value = "组织id",required = true),
            @ApiImplicitParam(name = "type",value = "模块类型，0领导人，1下属机构，2联系人，3下属企业",required = true),
            @ApiImplicitParam(name = "name",value = "模块名称",required = true)
    })
    @ApiOperation(value = "插入组织模块，返回模块id",tags = "新增")
    public Result<Long> insert(OrganizationModule organizationModule){
        organizationModuleService.save(organizationModule);
        AdminOperationLog adminOperationLog = new AdminOperationLog();
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            try {
                Object principal = subject.getPrincipal();
                Long adminId = Long.valueOf(principal.toString());
                adminOperationLog.setAdminId(adminId);
                adminOperationLog.setOperatorType("新增");
                adminOperationLog.setInterfaceDesc("插入组织模块:"+ StrUtil.blankToDefault(organizationModule.getName(),""));
            } catch (NumberFormatException e) {
                adminOperationLog.setAdminId(0L);
            }
        }
        adminOperationLogService.save(adminOperationLog);
        return Result.success(organizationModule.getId());
    }

    @PostMapping("update")
    @ApiOperationSupport(ignoreParameters = {"deleted","createTime","updateTime"})
    @ApiOperation(value = "修改模块名",tags = "修改")
    public Result<Boolean> updateById(OrganizationModule organizationModule){
        organizationModuleService.updateById(organizationModule);
        AdminOperationLog adminOperationLog = new AdminOperationLog();
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            try {
                Object principal = subject.getPrincipal();
                Long adminId = Long.valueOf(principal.toString());
                adminOperationLog.setAdminId(adminId);
                adminOperationLog.setOperatorType("修改");
                adminOperationLog.setInterfaceDesc("修改模块名:"+ StrUtil.blankToDefault(organizationModule.getName(),""));
            } catch (NumberFormatException e) {
                adminOperationLog.setAdminId(0L);
            }
        }
        adminOperationLogService.save(adminOperationLog);
        return Result.success(true);
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除组织模块",tags = "删除")
    @ApiImplicitParam(name = "id",value = "模块id",required = true)
    public Result<Boolean> delete(@RequestParam Long id){
        OrganizationModule organizationModule = organizationModuleService.getById(id);
        Assert.notNull(organizationModule,"组织模块不存在");
        organizationModuleService.deleteDataAboutThis(id);
        // 删除相关的数据
        organizationModuleService.removeById(id);

        AdminOperationLog adminOperationLog = new AdminOperationLog();
        Subject subject = SecurityUtils.getSubject();
        if(subject.isAuthenticated()){
            try {
                Object principal = subject.getPrincipal();
                Long adminId = Long.valueOf(principal.toString());
                adminOperationLog.setAdminId(adminId);
                adminOperationLog.setOperatorType("删除");
                adminOperationLog.setInterfaceDesc("删除组织模块:"+ StrUtil.blankToDefault(organizationModule.getName(),""));
            } catch (NumberFormatException e) {
                adminOperationLog.setAdminId(0L);
            }
        }
        adminOperationLogService.save(adminOperationLog);
        return Result.success(true);
    }

    @ApiOperation(value = "组织模块排序",tags = "修改")
    @PostMapping("update/seq")
    public Result<Boolean> updateSeq(@RequestParam Long moduleId, @RequestBody List<OrganizationModuleSeqDTO> organizationModuleSeqDTOList){
        organizationModuleService.updateSeq(moduleId,organizationModuleSeqDTOList);
        return Result.success(true);
    }
    @ApiOperation(value = "组织模块复制下属机构",tags = "新增")
    @PostMapping("copy")
    public Result<Boolean> copy(@RequestParam Long sourceModuleId,@RequestParam Long targetModuleId){
        organizationModuleService.copy(sourceModuleId,targetModuleId);
        return Result.success(true);
    }

    @PostMapping("/random/copy")
    @ApiOperation(value = "随机拷贝同级的机构",tags = "新增")
    public Result<Boolean> randomCopy(@RequestParam Long moduleId){

        organizationModuleService.randomCopy(moduleId);
        return Result.success(true);
    }

}

