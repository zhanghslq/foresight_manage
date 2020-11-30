package com.zhs.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.DropDownBoxTypeEnum;
import com.zhs.common.constant.RootTypeEnum;
import com.zhs.common.constant.ScopeEnum;
import com.zhs.exception.MyException;
import com.zhs.mapper.LeaderMapper;
import com.zhs.model.bo.LongBO;
import com.zhs.model.dto.LeaderImportConvertDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.*;
import com.zhs.util.EasyExcelUtil;
import com.zhs.entity.*;
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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private OrganizationModuleService organizationModuleService;
    @Autowired
    private ResumeService resumeService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private DownBoxDataService downBoxDataService;
    @Autowired
    private LeaderMapper leaderMapper;

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
        Integer type;
        OrganizationModule organizationModule = organizationModuleService.getById(moduleId);
        if(!Objects.isNull(organizationModule.getIsCompany())&&organizationModule.getIsCompany()==1){
            // 企业
            Company company = companyService.getById(organizationModule.getOrganizationId());
            Assert.notNull(company,"企业不存在");
            type = RootTypeEnum.COMPANY.getId();
        }else {
            Organization organization = organizationService.getById(organizationModule.getOrganizationId());
            Assert.notNull(organization,"组织不存在");
            type=organization.getType();

        }
        Assert.notNull(organizationModule,"下属机构模块不能为空");
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
            leader.setType(type);
            String levelName = readBook.getLevelName();
            // 需要区分几种类型，来用不同的type查
            List<DownBoxData> list;
            if(RootTypeEnum.PARTY.getId().equals(type)){
                list= downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), ScopeEnum.PARTY.getId());
            }else if(RootTypeEnum.GOVERNMENT.getId().equals(type)){
                list= downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), ScopeEnum.GOVERNMENT.getId());
            }else if(RootTypeEnum.LEGAL.getId().equals(type)){
                list= downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), ScopeEnum.LEGAL.getId());
            }else if(RootTypeEnum.POLITICAL_PARTICIPATION.getId().equals(type)){
                list= downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), ScopeEnum.POLITICAL_PARTICIPATION.getId());
            }else if(RootTypeEnum.MILITARY.getId().equals(type)){
                list= downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), ScopeEnum.ARMY.getId());
            }else if(RootTypeEnum.COMPANY.getId().equals(type)){
                list= downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.LEADER_LEVEL.getId(), ScopeEnum.COMPANY.getId());
            } else  {
                throw new MyException("不属于所属类型");
            }
            if(list.size()>0){
                List<DownBoxData> thisNameList = list.stream().filter(downBoxData -> Objects.nonNull(downBoxData.getName()) && downBoxData.getName().equals(levelName)).collect(Collectors.toList());
                if(thisNameList.size()>0){
                    leader.setLevelId(thisNameList.get(0).getId().longValue());
                    // idArray
                    List<Integer> ids = new ArrayList<>();
                    ids.add(thisNameList.get(0).getId());
                    Map<Integer, DownBoxData> map = list.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData -> downBoxData));
                    ResumeServiceImpl.dealArray(ids,map);
                    leader.setLevelIdArray(JSONArray.toJSONString(ids));
                }

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

    @Override
    public List<LongBO> countByOrganizationId(List<Long> organizationIdList) {

        return leaderMapper.countByOrganizationId(organizationIdList);
    }
}
