package com.zhs.backmanageb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.common.Result;
import com.zhs.backmanageb.common.constant.DropDownBoxTypeEnum;
import com.zhs.backmanageb.common.constant.RootTypeEnum;
import com.zhs.backmanageb.entity.*;
import com.zhs.backmanageb.exception.MyException;
import com.zhs.backmanageb.mapper.LeaderMapper;
import com.zhs.backmanageb.model.dto.LeaderImportConvertDTO;
import com.zhs.backmanageb.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.backmanageb.util.EasyExcelUtil;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 领导人 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class LeaderServiceImpl extends ServiceImpl<LeaderMapper, Leader> implements LeaderService {

    @Autowired
    private CommonDataService commonDataService;
    @Autowired
    private OrganizationModuleService organizationModuleService;
    @Autowired
    private ResumeService resumeService;
    @Autowired
    private OrganizationService organizationService;

    @Override
    public void listUpload(Long moduleId, MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (StringUtils.isEmpty(fileName)){
            throw new MyException("文件不能为空");
        }
        // 获取文件后缀
        String prefix=fileName.substring(fileName.lastIndexOf("."));
        if (!prefix.toLowerCase().contains("xls") && !prefix.toLowerCase().contains("xlsx") ){
            throw new MyException("文件格式异常，请上传Excel文件格式");
        }
        // 防止生成的临时文件重复-建议使用UUID
        final File excelFile;
        try {
            excelFile = File.createTempFile(System.currentTimeMillis()+"", prefix);
            file.transferTo(excelFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new MyException("文件上传失败");
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(excelFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new MyException("文件上传失败");
        }
        OrganizationModule organizationModule = organizationModuleService.getById(moduleId);
        Assert.notNull(organizationModule,"下属机构模块不能为空");
        Organization organization = organizationService.getById(organizationModule.getOrganizationId());
        Assert.notNull(organization,"组织不存在");
        Integer type =organization.getType();
        List<LeaderImportConvertDTO> readBooks = EasyExcelUtil.readListFrom(fileInputStream, LeaderImportConvertDTO.class);
        excelFile.delete();
        // 对数据处理
        List<Leader> result = new ArrayList<>();
        for (LeaderImportConvertDTO readBook : readBooks) {
            Leader leader = new Leader();
            leader.setRealName(readBook.getRealName());
            leader.setModuleId(moduleId);
            leader.setOrganizationId(organizationModule.getOrganizationId());
            leader.setPosition(readBook.getPosition());
            String levelName = readBook.getLevelName();
            QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
            commonDataQueryWrapper.eq("name",levelName);
            // 需要区分几种类型，来用不同的type查
            if(RootTypeEnum.PARTY.getId().equals(type)){
                commonDataQueryWrapper.eq("type", DropDownBoxTypeEnum.PARTY_AFFAIRS_ORGANIZATION_LEVEL.getId());
            }else if(RootTypeEnum.GOVERNMENT.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.GOVERNMENT_AFFAIRS_ORGANIZATION_LEVEL.getId());
            }else if(RootTypeEnum.LEGAL.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.LEGAL_AFFAIRS_ORGANIZATION_LEVEL.getId());
            }else if(RootTypeEnum.POLITICAL_PARTICIPATION.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.POLITICAL_PARTICIPATION_ORGANIZATION_LEVEL.getId());
            }else if(RootTypeEnum.MILITARY.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.ARMY_ORGANIZATION_LEVEL.getId());
            }/*else if(RootTypeEnum.COMPANY.getId().equals(type)){

                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.COMPANY_LEVEL);
            }*/else {
                throw new MyException("不属于所属类型");
            }
            List<CommonData> list = commonDataService.list(commonDataQueryWrapper);
            if(list.size()>0){
                leader.setLevelId(list.get(0).getId());
            }
            // 拿名字去简历里面查一下
            QueryWrapper<Resume> resumeQueryWrapper = new QueryWrapper<>();
            resumeQueryWrapper.eq("real_name",readBook.getRealName());
            List<Resume> resumeList = resumeService.list(resumeQueryWrapper);
            if(resumeList.size()>0){
                leader.setHasResume(1);
            }else {
                leader.setHasResume(0);
            }
            result.add(leader);
        }
        if(result.size()>0){
            saveBatch(result);
        }
    }
}
