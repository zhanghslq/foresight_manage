package com.zhs.backmanageb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.entity.Leader;
import com.zhs.backmanageb.entity.Organization;
import com.zhs.backmanageb.entity.OrganizationType;
import com.zhs.backmanageb.mapper.OrganizationMapper;
import com.zhs.backmanageb.model.bo.OrganizationBO;
import com.zhs.backmanageb.service.ContactsService;
import com.zhs.backmanageb.service.LeaderService;
import com.zhs.backmanageb.service.OrganizationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.backmanageb.service.OrganizationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    private OrganizationTypeService organizationTypeService;


    @Override
    public OrganizationBO queryByOrganizationType(Long organizationTypeId, Long areaId) {
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
        OrganizationBO organizationBO = new OrganizationBO();
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("organization_type_id",organizationTypeIds);
        if(Objects.isNull(areaId)){
            queryWrapper.eq("area_id",areaId);
        }
        List<Organization> list = list(queryWrapper);
        if (list.size()==0){
            return organizationBO;
        }
        Organization organization = list.get(0);
        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("parentId",organization.getId());
        // 查到的子类组织
        List<Organization> organizationChildren = list(organizationQueryWrapper);
        organizationBO.setOrganizationChildren(organizationChildren);
        //自身
        organizationBO.setOrganization(organization);
        // 联系人和领导人
        getContactAndLeader(organizationBO, organization.getId());
        return organizationBO;
    }

    @Override
    public OrganizationBO queryByParentId(Long id) {
        OrganizationBO organizationBO = new OrganizationBO();

        getContactAndLeader(organizationBO,id);
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parentId",id);
        List<Organization> list = list(queryWrapper);
        organizationBO.setOrganizationChildren(list);
        return organizationBO;
    }
    private void getContactAndLeader(OrganizationBO organizationBO, Long organizationId) {

        QueryWrapper<Contacts> contactsQueryWrapper = new QueryWrapper<>();
        contactsQueryWrapper.eq("organization_id", organizationId);
        List<Contacts> contactsList = contactsService.list(contactsQueryWrapper);
        organizationBO.setContacts(contactsList);

        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("organization_id", organizationId);
        List<Leader> leaderList = leaderService.list(leaderQueryWrapper);
        organizationBO.setLeaders(leaderList);
    }
}
