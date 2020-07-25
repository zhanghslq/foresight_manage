package com.zhs.backmanageb.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.entity.Contacts;
import com.zhs.backmanageb.entity.Leader;
import com.zhs.backmanageb.entity.Organization;
import com.zhs.backmanageb.mapper.OrganizationMapper;
import com.zhs.backmanageb.model.bo.OrganizationBO;
import com.zhs.backmanageb.service.ContactsService;
import com.zhs.backmanageb.service.LeaderService;
import com.zhs.backmanageb.service.OrganizationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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




    @Override
    public OrganizationBO queryByOrganizationType(Long organizationTypeId) {
        OrganizationBO organizationBO = new OrganizationBO();
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("organization_type_id",organizationTypeId);
        List<Organization> list = list(queryWrapper);
        if (list.size()==0){
            return organizationBO;
        }
        getContactAndLeader(organizationBO, list);
        return organizationBO;
    }

    @Override
    public OrganizationBO queryByParentId(Long id) {
        OrganizationBO organizationBO = new OrganizationBO();
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parentId",id);
        List<Organization> list = list(queryWrapper);
        if (list.size()==0){
            return organizationBO;
        }
        organizationBO.setOrganizations(list);
        getContactAndLeader(organizationBO,id);
        return organizationBO;
    }

    private void getContactAndLeader(OrganizationBO organizationBO, List<Organization> list) {
        organizationBO.setOrganizations(list);
        List<Integer> organizationIds = list.stream().map(Organization::getId).collect(Collectors.toList());
        QueryWrapper<Contacts> contactsQueryWrapper = new QueryWrapper<>();
        contactsQueryWrapper.in("organization_id", organizationIds);
        List<Contacts> contactsList = contactsService.list(contactsQueryWrapper);
        organizationBO.setContacts(contactsList);

        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.in("organization_id", organizationIds);
        List<Leader> leaderList = leaderService.list(leaderQueryWrapper);
        organizationBO.setLeaders(leaderList);
    }
    private void getContactAndLeader(OrganizationBO organizationBO, Long organizationId) {

        QueryWrapper<Contacts> contactsQueryWrapper = new QueryWrapper<>();
        contactsQueryWrapper.eq("id", organizationId);
        List<Contacts> contactsList = contactsService.list(contactsQueryWrapper);
        organizationBO.setContacts(contactsList);

        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("organization_id", organizationId);
        List<Leader> leaderList = leaderService.list(leaderQueryWrapper);
        organizationBO.setLeaders(leaderList);
    }
}
