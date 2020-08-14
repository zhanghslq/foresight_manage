package com.zhs.backmanageb.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.backmanageb.common.constant.ModuleTypeEnum;
import com.zhs.backmanageb.entity.*;
import com.zhs.backmanageb.mapper.OrganizationMapper;
import com.zhs.backmanageb.model.bo.OrganizationHasParentBO;
import com.zhs.backmanageb.model.bo.OrganizationModuleBO;
import com.zhs.backmanageb.model.bo.OrganizationTagBO;
import com.zhs.backmanageb.model.vo.OrganizationVO;
import com.zhs.backmanageb.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @Autowired
    private OrganizationTagService organizationTagService;

    @Autowired
    private ModuleContactsService moduleContactsService;

    @Autowired
    private OrganizationMapper organizationMapper;

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
        if(!Objects.isNull(areaId)){
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

    @Override
    public void dealTags(Long id, List<OrganizationTagBO> tags) {
        if(Objects.isNull(tags)||tags.size()==0){
            QueryWrapper<OrganizationTag> organizationTagQueryWrapper = new QueryWrapper<>();
            organizationTagQueryWrapper.eq("organization_id",id);
            organizationTagQueryWrapper.eq("is_company",0);
            organizationTagService.remove(organizationTagQueryWrapper);
            return;
        }
        QueryWrapper<OrganizationTag> organizationTagQueryWrapper = new QueryWrapper<>();
        organizationTagQueryWrapper.eq("organization_id",id);
        organizationTagQueryWrapper.eq("is_company",0);
        List<OrganizationTag> organizationTags = organizationTagService.list(organizationTagQueryWrapper);
        List<Long> tagIds = organizationTags.stream().map(OrganizationTag::getId).collect(Collectors.toList());
        List<Long> boIds = tags.stream().filter(organizationTagBO -> !Objects.isNull(organizationTagBO.getId())).map(OrganizationTagBO::getId).collect(Collectors.toList());
        tagIds.removeAll(boIds);
        if(tagIds.size()>0){
            organizationTagService.removeByIds(tagIds);
        }
        List<OrganizationTag> organizationTagArrayList = new ArrayList<>();
        for (OrganizationTagBO tag : tags) {
            OrganizationTag organizationTag = new OrganizationTag();
            organizationTag.setId(tag.getId());
            organizationTag.setName(tag.getName());
            organizationTag.setIsCompany(0);
            organizationTag.setOrganizationId(id);
            organizationTagArrayList.add(organizationTag);
        }
        organizationTagService.saveOrUpdateBatch(organizationTagArrayList);
    }

    @Override
    public OrganizationHasParentBO listParentById(Long organizationId) {
        OrganizationHasParentBO organizationHasParentBO = organizationMapper.listParentById(organizationId);

        return organizationHasParentBO;
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

        QueryWrapper<OrganizationTag> organizationTagQueryWrapper = new QueryWrapper<>();
        organizationTagQueryWrapper.eq("organization_id",organizationId);
        organizationTagQueryWrapper.eq("is_company",0);
        List<OrganizationTag> list = organizationTagService.list(organizationTagQueryWrapper);
        if(list.size()>0){
            ArrayList<OrganizationTagBO> organizationTagBOS = new ArrayList<>();
            for (OrganizationTag organizationTag : list) {
                OrganizationTagBO organizationTagBO = new OrganizationTagBO();
                BeanUtil.copyProperties(organizationTag,organizationTagBO);
                organizationTagBOS.add(organizationTagBO);
            }
            organizationVO.setTags(organizationTagBOS);
        }


        QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
        organizationModuleQueryWrapper.eq("organization_id",organizationId);
        organizationModuleQueryWrapper.eq("is_company",0);
        organizationModuleQueryWrapper.orderByAsc("seq");
        List<OrganizationModule> organizationModuleList = organizationModuleService.list(organizationModuleQueryWrapper);




        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("organization_id", organizationId);
        List<Leader> leaderList = leaderService.list(leaderQueryWrapper);

        Map<Long, List<Leader>> leaderMap = leaderList.stream().collect(Collectors.groupingBy(item -> {
            if (item.getModuleId() == null) {
                return 0L;
            }
            return item.getModuleId();
        }));


        List<OrganizationModuleBO> organizationModuleBOS = new ArrayList<>();
        for (OrganizationModule organizationModule : organizationModuleList) {
            OrganizationModuleBO organizationModuleBO = new OrganizationModuleBO();
            organizationModuleBO.setModuleName(organizationModule.getName());
            organizationModuleBO.setModuleId(organizationModule.getId());
            if(ModuleTypeEnum.LEADER.getId().equals(organizationModule.getType())){
                organizationModuleBO.setType(ModuleTypeEnum.LEADER.getId());
                List<Leader> leaders = leaderMap.get(organizationModule.getId());
                if(!Objects.isNull(leaders)){
                    organizationModuleBO.setLeaders(leaders.stream().sorted(Comparator.comparingInt(Leader::getSeq)).collect(Collectors.toList()));
                }
            }else if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(organizationModule.getType())){
                organizationModuleBO.setType(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId());
                List<Organization> organizations = organizationMap.get(organizationModule.getId());
                if(!Objects.isNull(organizations)){
                    organizationModuleBO.setOrganizationChildren(organizations.stream().sorted(Comparator.comparingInt(Organization::getSeq)).collect(Collectors.toList()));
                }
            }else if(ModuleTypeEnum.CONTACTS.getId().equals(organizationModule.getType())){
                organizationModuleBO.setType(ModuleTypeEnum.CONTACTS.getId());
                // 这个联系人列表就不能用了
                // 从organization_module获取moduleId，从module_contacts根据moduleId获取联系人id

                // 可以直接根据moduleId进行查询
                QueryWrapper<ModuleContacts> moduleContactsQueryWrapper = new QueryWrapper<>();
                moduleContactsQueryWrapper.eq("module_id",organizationModule.getId());
                List<ModuleContacts> moduleContactsList = moduleContactsService.list(moduleContactsQueryWrapper);
                // 根据模块联系人查
                List<Contacts> contactsArrayList = new ArrayList<>();
                if(moduleContactsList.size()>0){
                    Map<Long, Integer> map = moduleContactsList.stream().collect(Collectors.toMap(ModuleContacts::getContactId, ModuleContacts::getSeq,(k1,k2)->k1));
                    // 查contact集合
                    List<Long> contactIds = moduleContactsList.stream().map(ModuleContacts::getContactId).collect(Collectors.toList());
                    List<Contacts> contacts = contactsService.listByIds(contactIds);
                    contactsArrayList = contacts.stream().sorted(Comparator.comparing(Contacts::getId, (x, y) -> {
                        Integer integer = map.get(x);
                        Integer integerY = map.get(y);
                        if (Objects.isNull(integer)) {
                            integer = 0;
                        }
                        if (Objects.isNull(integerY)) {
                            integerY = 0;
                        }
                        return integer.compareTo(integerY);
                    })).collect(Collectors.toList());

                }
                organizationModuleBO.setContacts(contactsArrayList);
            }
            organizationModuleBOS.add(organizationModuleBO);
        }
        organizationVO.setOrganizationModules(organizationModuleBOS);
    }
}
