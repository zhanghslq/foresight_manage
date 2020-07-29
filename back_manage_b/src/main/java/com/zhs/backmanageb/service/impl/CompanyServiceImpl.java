package com.zhs.backmanageb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.common.constant.ModuleTypeEnum;
import com.zhs.backmanageb.entity.*;
import com.zhs.backmanageb.mapper.CompanyMapper;
import com.zhs.backmanageb.model.bo.CompanyModuleBO;
import com.zhs.backmanageb.model.bo.OrganizationModuleBO;
import com.zhs.backmanageb.model.vo.CompanyVO;
import com.zhs.backmanageb.model.vo.OrganizationVO;
import com.zhs.backmanageb.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.CheckedOutputStream;

/**
 * <p>
 * 企业 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, Company> implements CompanyService {
    @Autowired
    private LeaderService leaderService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private OrganizationTypeService organizationTypeService;

    @Autowired
    private OrganizationModuleService organizationModuleService;


    @Override
    public CompanyVO queryByOrganizationType(Long organizationTypeId, Long areaId) {
        List<Long> organizationTypeIds = new ArrayList<>();
        // 需要先判断组织类别是不是还有子类，还有子类的话就取子类中的
        QueryWrapper<OrganizationType> organizationTypeQueryWrapper = new QueryWrapper<>();
        organizationTypeQueryWrapper.eq("parent_id",organizationTypeId);
        List<OrganizationType> typeList = organizationTypeService.list(organizationTypeQueryWrapper);
        if(typeList.size()!=0){
            organizationTypeIds.addAll(typeList.stream().map(OrganizationType::getId).collect(Collectors.toList()));
        }else {
            organizationTypeIds.add(organizationTypeId);
        }
        CompanyVO companyVO = new CompanyVO();
        QueryWrapper<Company> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("organization_type_id",organizationTypeIds);
        if(!Objects.isNull(areaId)){
            queryWrapper.eq("area_id",areaId);
        }
        List<Company> list = list(queryWrapper);
        if (list.size()==0){
            return companyVO;
        }
        Company company = list.get(0);
        // 这里需要对子类组织进行分类，对联系人，领导人进行分模块
        companyVO.setCompany(company);

        // 联系人和领导人
        getContactAndLeader(companyVO, company.getId());
        return companyVO;
    }

    @Override
    public CompanyVO queryByParentId(Long id) {
        CompanyVO companyVO = new CompanyVO();
        companyVO.setCompany(getById(id));
        getContactAndLeader(companyVO,id);
        return companyVO;
    }

    private void getContactAndLeader(CompanyVO companyVO, Long organizationId) {
        QueryWrapper<Company> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("parent_id",organizationId);
        // 查到的子类组织
        List<Company> companyChildren = list(organizationQueryWrapper);

        Map<Long, List<Company>> companyMap = companyChildren.stream().collect(Collectors.groupingBy(item -> {
            if (item.getModuleId() == null) {
                return 0L;
            }
            return item.getModuleId();
        }));


        QueryWrapper<Contacts> contactsQueryWrapper = new QueryWrapper<>();
        contactsQueryWrapper.eq("organization_id", organizationId);
        List<Contacts> contactsList = contactsService.list(contactsQueryWrapper);
        Map<Long, List<Contacts>> contactsMap = contactsList.stream().collect(Collectors.groupingBy(item -> {
            if (item.getModuleId() == null) {
                return 0L;
            }
            return item.getModuleId();
        }));


        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("organization_id", organizationId);
        List<Leader> leaderList = leaderService.list(leaderQueryWrapper);

        Map<Long, List<Leader>> leaderMap = leaderList.stream().collect(Collectors.groupingBy(item -> {
            if (item.getModuleId() == null) {
                return 0L;
            }
            return item.getModuleId();
        }));

        QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
        organizationModuleQueryWrapper.eq("organization_id",organizationId);
        List<OrganizationModule> organizationModuleList = organizationModuleService.list(organizationModuleQueryWrapper);
        List<CompanyModuleBO> companyModuleBOS = new ArrayList<>();
        for (OrganizationModule organizationModule : organizationModuleList) {
            CompanyModuleBO companyModuleBO = new CompanyModuleBO();
            companyModuleBO.setModuleName(organizationModule.getName());
            companyModuleBO.setModuleId(organizationModule.getId());
            if(ModuleTypeEnum.LEADER.getId().equals(organizationModule.getType())){
                companyModuleBO.setType(ModuleTypeEnum.LEADER.getId());
                companyModuleBO.setLeaders(leaderMap.get(organizationModule.getId()));
            }else if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(organizationModule.getType())){
                companyModuleBO.setType(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId());
                companyModuleBO.setCompanyChildren(companyMap.get(organizationModule.getId()));
            }else if(ModuleTypeEnum.CONTACTS.getId().equals(organizationModule.getType())){
                companyModuleBO.setType(ModuleTypeEnum.CONTACTS.getId());
                companyModuleBO.setContacts(contactsMap.get(organizationModule.getId()));
            }
            companyModuleBOS.add(companyModuleBO);
        }
        companyVO.setCompanyModules(companyModuleBOS);
    }
}
