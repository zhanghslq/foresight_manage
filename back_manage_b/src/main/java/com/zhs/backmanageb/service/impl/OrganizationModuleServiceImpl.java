package com.zhs.backmanageb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.collect.Maps;
import com.zhs.backmanageb.common.constant.ModuleTypeEnum;
import com.zhs.backmanageb.entity.*;
import com.zhs.backmanageb.mapper.OrganizationModuleMapper;
import com.zhs.backmanageb.model.dto.OrganizationModuleSeqDTO;
import com.zhs.backmanageb.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

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
}
