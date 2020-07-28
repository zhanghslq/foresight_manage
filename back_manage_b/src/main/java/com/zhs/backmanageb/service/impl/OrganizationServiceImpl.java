package com.zhs.backmanageb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.common.constant.ModuleTypeEnum;
import com.zhs.backmanageb.entity.*;
import com.zhs.backmanageb.mapper.OrganizationMapper;
import com.zhs.backmanageb.model.bo.OrganizationModuleBO;
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

/**
 * <p>
 * 组织机构 服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements OrganizationService {

    @Autowired
    private LeaderService leaderService;

    @Autowired
    private ContactsService contactsService;

    @Autowired
    private OrganizationModuleService organizationModuleService;

    @Autowired
    private OrganizationTypeService organizationTypeService;


    @Override
    public OrganizationVO queryByOrganizationType(Long organizationTypeId, Long areaId) {
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
        OrganizationVO organizationVO = new OrganizationVO();
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("organization_type_id",organizationTypeIds);
        if(Objects.isNull(areaId)){
            queryWrapper.eq("area_id",areaId);
        }
        List<Organization> list = list(queryWrapper);
        if (list.size()==0){
            return organizationVO;
        }
        Organization organization = list.get(0);
        // 这里需要对子类组织进行分类，对联系人，领导人进行分模块
        organizationVO.setOrganization(organization);

        // 联系人和领导人
        getContactAndLeader(organizationVO, organization.getId());
        return organizationVO;
    }

    @Override
    public OrganizationVO queryByParentId(Long id) {
        OrganizationVO organizationVO = new OrganizationVO();
        organizationVO.setOrganization(getById(id));
        getContactAndLeader(organizationVO,id);
        return organizationVO;
    }
    private void getContactAndLeader(OrganizationVO organizationVO, Long organizationId) {
        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("parent_id",organizationId);
        // 查到的子类组织
        List<Organization> organizationChildren = list(organizationQueryWrapper);

        Map<Long, List<Organization>> organizationMap = organizationChildren.stream().collect(Collectors.groupingBy(item -> {
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
        List<OrganizationModuleBO> organizationModuleBOS = new ArrayList<>();
        for (OrganizationModule organizationModule : organizationModuleList) {
            OrganizationModuleBO organizationModuleBO = new OrganizationModuleBO();
            organizationModuleBO.setModuleName(organizationModule.getName());
            organizationModuleBO.setModuleId(organizationModule.getId());
            if(ModuleTypeEnum.LEADER.getId().equals(organizationModule.getType())){
                organizationModuleBO.setType(ModuleTypeEnum.LEADER.getId());
                organizationModuleBO.setLeaders(leaderMap.get(organizationModule.getId()));
            }else if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(organizationModule.getType())){
                organizationModuleBO.setType(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId());
                organizationModuleBO.setOrganizationChildren(organizationMap.get(organizationModule.getId()));
            }else if(ModuleTypeEnum.CONTACTS.getId().equals(organizationModule.getType())){
                organizationModuleBO.setType(ModuleTypeEnum.CONTACTS.getId());
                organizationModuleBO.setContacts(contactsMap.get(organizationModule.getId()));
            }
            organizationModuleBOS.add(organizationModuleBO);
        }
        organizationVO.setOrganizationModules(organizationModuleBOS);
    }
}
