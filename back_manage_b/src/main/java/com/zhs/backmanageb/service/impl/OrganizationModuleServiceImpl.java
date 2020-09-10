package com.zhs.backmanageb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Maps;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.zhs.backmanageb.common.constant.ModuleTypeEnum;
import com.zhs.backmanageb.entity.*;
import com.zhs.backmanageb.exception.MyException;
import com.zhs.backmanageb.mapper.OrganizationModuleMapper;
import com.zhs.backmanageb.model.dto.OrganizationModuleSeqDTO;
import com.zhs.backmanageb.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import sun.security.pkcs11.Secmod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织模块 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-27
 */
@Service
public class OrganizationModuleServiceImpl extends ServiceImpl<OrganizationModuleMapper, OrganizationModule> implements OrganizationModuleService {

    @Autowired
    private LeaderService leaderService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private ModuleContactsService moduleContactsService;

    @Override
    public void deleteDataAboutThis(Long id) {
        // 删除模块id相关的
        OrganizationModule byId = getById(id);
        Assert.notNull(byId,"模块不存在");
        Integer type = byId.getType();
        if(ModuleTypeEnum.LEADER.getId().equals(type)){
            QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
            leaderQueryWrapper.eq("module_id",id);
            leaderService.remove(leaderQueryWrapper);
        }else if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(type)){
            QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
            organizationQueryWrapper.eq("module_id",id);
            organizationService.remove(organizationQueryWrapper);
        }else if(ModuleTypeEnum.CONTACTS.getId().equals(type)){
            QueryWrapper<ModuleContacts> contactsQueryWrapper = new QueryWrapper<>();
            contactsQueryWrapper.eq("module_id",id);
            moduleContactsService.remove(contactsQueryWrapper);
        }else if(ModuleTypeEnum.COMPANY_CHILDREN.getId().equals(type)){
            QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
            companyQueryWrapper.eq("module_id",id);
            companyService.remove(companyQueryWrapper);
        }
    }

    @Override
    public void updateSeq(Long moduleId, List<OrganizationModuleSeqDTO> organizationModuleSeqDTOList) {
        OrganizationModule organizationModule = getById(moduleId);
        Integer type = organizationModule.getType();
        if(ModuleTypeEnum.LEADER.getId().equals(type)){
            List<Leader> result = new ArrayList<>();
            for (OrganizationModuleSeqDTO organizationModuleSeqDTO : organizationModuleSeqDTOList) {
                Leader leader = new Leader();
                BeanUtil.copyProperties(organizationModuleSeqDTO,leader);
                result.add(leader);
            }
            leaderService.updateBatchById(result);
        }else if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(type)){
            List<Organization> result = new ArrayList<>();
            for (OrganizationModuleSeqDTO organizationModuleSeqDTO : organizationModuleSeqDTOList) {
                Organization organization = new Organization();
                BeanUtil.copyProperties(organizationModuleSeqDTO,organization);
                result.add(organization);
            }
            organizationService.updateBatchById(result);
        }else if(ModuleTypeEnum.CONTACTS.getId().equals(type)){
            List<Contacts> result = new ArrayList<>();
            for (OrganizationModuleSeqDTO organizationModuleSeqDTO : organizationModuleSeqDTOList) {
                UpdateWrapper<ModuleContacts> moduleContactsUpdateWrapper = new UpdateWrapper<>();
                moduleContactsUpdateWrapper.set("seq", organizationModuleSeqDTO.getSeq());
                moduleContactsUpdateWrapper.eq("module_id",organizationModuleSeqDTO.getModuleId());
                moduleContactsUpdateWrapper.eq("contact_id",organizationModuleSeqDTO.getId());
                moduleContactsService.update(moduleContactsUpdateWrapper);
            }
        }else if(ModuleTypeEnum.COMPANY_CHILDREN.getId().equals(type)){
            List<Company> result = new ArrayList<>();
            for (OrganizationModuleSeqDTO organizationModuleSeqDTO : organizationModuleSeqDTOList) {
                Company company = new Company();
                BeanUtil.copyProperties(organizationModuleSeqDTO,company);
                result.add(company);
            }
            companyService.updateBatchById(result);
        }
    }

    @Override
    public void copy(Long sourceModuleId, Long targetModuleId) {
        OrganizationModule sourceOrganizationModule = getById(sourceModuleId);
        OrganizationModule targetOrganizationModule = getById(targetModuleId);
        Assert.notNull(sourceOrganizationModule,"原模块不存在");
        Assert.notNull(targetOrganizationModule,"目标模块不存在");
        Integer sourceOrganizationModuleType = sourceOrganizationModule.getType();
        Integer targetOrganizationModuleType = targetOrganizationModule.getType();

        if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(sourceOrganizationModuleType)
                &&ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(targetOrganizationModuleType)){
            QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
            organizationQueryWrapper.eq("module_id",sourceModuleId);
            List<Organization> sourceOrganizationList = organizationService.list(organizationQueryWrapper);
            for (Organization organization : sourceOrganizationList) {
                organization.setId(null);
                Subject subject = SecurityUtils.getSubject();
                organization.setAdminId(Long.valueOf(subject.getPrincipal().toString()));
                organization.setParentId(targetOrganizationModule.getOrganizationId());
                organization.setCreateTime(new Date());
                organization.setUpdateTime(new Date());
                organization.setModuleId(targetModuleId);
            }
            organizationService.saveBatch(sourceOrganizationList);
        }else if(ModuleTypeEnum.COMPANY_CHILDREN.getId().equals(sourceOrganizationModuleType)
        && ModuleTypeEnum.COMPANY_CHILDREN.getId().equals(targetOrganizationModuleType)){
            QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
            companyQueryWrapper.eq("module_id",sourceModuleId);
            List<Company> sourceOrganizationList = companyService.list(companyQueryWrapper);
            for (Company company : sourceOrganizationList) {
                company.setId(null);
                Subject subject = SecurityUtils.getSubject();
                company.setAdminId(Long.valueOf(subject.getPrincipal().toString()));
                company.setParentId(targetOrganizationModule.getOrganizationId());
                company.setCreateTime(new Date());
                company.setUpdateTime(new Date());
                company.setModuleId(targetModuleId);
            }
            companyService.saveBatch(sourceOrganizationList);
        }else {
            throw new  MyException("模块id错误");
        }

    }

    @Override
    public void randomCopy(Long moduleId) {
        // 寻找与当前模块同级别的模块
        OrganizationModule organizationModule = getById(moduleId);
        Assert.notNull(organizationModule,"模块不存在");
        Integer type = organizationModule.getType();
        if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(type)){
            copyOrganization(moduleId, organizationModule);
        }

    }

    private void copyOrganization(Long moduleId, OrganizationModule organizationModule) {
        Long organizationId = organizationModule.getOrganizationId();
        Organization organization = organizationService.getById(organizationId);

        int maxLevel=100;
        int organizationLevel=0;
        // 查组织处于第几级
        while (organization.getParentId()!=0&&organizationLevel<maxLevel){
            organizationLevel++;
            organization=organizationService.getById(organization.getParentId());
        }

        // 查到组织类别
        Long organizationTypeId = organization.getOrganizationTypeId();

        // 查组织类别下的组织
        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("organization_type_id",organizationTypeId);
        // 查到的同类别的组织
        List<Organization> organizationList = organizationService.list(organizationQueryWrapper);
        Map<Long, List<Organization>> organizationMap = organizationList.stream().collect(Collectors.groupingBy(Organization::getModuleId));

        for (Organization organ : organizationList) {
            Organization organUse = new Organization();
            BeanUtil.copyProperties(organ,organUse);
            // 查级别
            int organLevel=0;
            // 查组织处于第几级
            while (organUse.getParentId()!=0&&organLevel<maxLevel){
                organLevel++;
                organUse=organizationService.getById(organUse.getParentId());
            }
            if(organLevel==organizationLevel){
                // 级别相同，查模块，以及模块下的组织
                QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
                organizationModuleQueryWrapper.eq("type", ModuleTypeEnum.ORGANIZATION_CHILDREN.getId());
                organizationModuleQueryWrapper.eq("organization_id",organ.getId());
                List<OrganizationModule> list = list(organizationModuleQueryWrapper);
                if(list.size()!=0){
                    for (OrganizationModule module : list) {
                        Long id = module.getId();
                        List<Organization> organizations = organizationMap.get(id);
                        if(!Objects.isNull(organizations)){
                            // 模块下有组织
                            copy(id,moduleId);
                            return;
                        }
                    }
                }

            }
        }
    }
}
