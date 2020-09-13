package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.constant.DropDownBoxTypeEnum;
import com.zhs.common.constant.ImportanceTypeEnum;
import com.zhs.common.constant.ModuleTypeEnum;
import com.zhs.common.constant.RootTypeEnum;
import com.zhs.exception.MyException;
import com.zhs.mapper.OrganizationMapper;
import com.zhs.model.bo.ContactsBO;
import com.zhs.model.bo.OrganizationHasParentBO;
import com.zhs.model.bo.OrganizationModuleBO;
import com.zhs.model.bo.OrganizationTagBO;
import com.zhs.model.dto.OrganizationImportConvertDTO;
import com.zhs.model.vo.OrganizationVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.*;
import com.zhs.util.EasyExcelUtil;
import com.zhs.entity.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    @Autowired
    private CommonDataService commonDataService;

    @Autowired
    private AreaService areaService;

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

    @Override
    public void listUpload(Long moduleId, MultipartFile file) {
        OrganizationModule organizationModule = organizationModuleService.getById(moduleId);
        Assert.notNull(organizationModule,"模块不存在");
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
//        Integer type = organizationModule.getType();
        Assert.notNull(organizationModule,"下属机构模块不能为空");
        Organization organizationById = getById(organizationModule.getOrganizationId());
        Assert.notNull(organizationById,"组织不存在");
        Integer type =organizationById.getType();
        List<OrganizationImportConvertDTO> readBooks = EasyExcelUtil.readListFrom(fileInputStream, OrganizationImportConvertDTO.class);
        excelFile.delete();
        List<Organization> result = new ArrayList<>();
        // 解析Excel
        for (OrganizationImportConvertDTO readBook : readBooks) {
            Organization organization = new Organization();
            organization.setModuleId(moduleId);
            organization.setParentId(organizationModule.getOrganizationId());
            Subject subject = SecurityUtils.getSubject();
            organization.setAdminId(Long.valueOf(subject.getPrincipal().toString()));
            organization.setAddressDetail(readBook.getAddressDetail());
            organization.setPerfectValue(readBook.getPerfectValue());
            organization.setImportance(ImportanceTypeEnum.getIdByName(readBook.getImportance()));
            organization.setSeq(readBook.getSeq());
            organization.setName(readBook.getName());
            organization.setType(type);
            String commonType = readBook.getCommonType();
            // 这个类型到库里查
            QueryWrapper<CommonData> commonDataQueryWrapper = new QueryWrapper<>();
            // 需要区分几种类型，来用不同的type查
            if(RootTypeEnum.PARTY.getId().equals(type)){
                commonDataQueryWrapper.eq("type", DropDownBoxTypeEnum.PARTY_AFFAIRS_ORGANIZATION_TYPE.getId());
            }else if(RootTypeEnum.GOVERNMENT.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.GOVERNMENT_AFFAIRS_ORGANIZATION_TYPE.getId());
            }else if(RootTypeEnum.LEGAL.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.LEGAL_AFFAIRS_ORGANIZATION_TYPE.getId());
            }else if(RootTypeEnum.POLITICAL_PARTICIPATION.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.POLITICAL_PARTICIPATION_ORGANIZATION_TYPE.getId());
            }else if(RootTypeEnum.MILITARY.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.ARMY_ORGANIZATION_TYPE.getId());
            }/*else if(RootTypeEnum.COMPANY.getId().equals(type)){
                commonDataQueryWrapper.eq("type",DropDownBoxTypeEnum.COMPANY_LEVEL);
            }*/else {
                throw new MyException("不属于所属类型");
            }
            QueryWrapper<CommonData> commonDataQueryWrapperSystem = new QueryWrapper<>();
            QueryWrapper<CommonData> commonDataQueryWrapperHierarchy = new QueryWrapper<>();
            QueryWrapper<CommonData> commonDataQueryWrapperLevel = new QueryWrapper<>();
            QueryWrapper<CommonData> systemWrapper = commonDataQueryWrapperSystem.eq("name", readBook.getSystem());
            QueryWrapper<CommonData> hierarchyWrapper = commonDataQueryWrapperHierarchy.eq("name", readBook.getHierarchy());
            QueryWrapper<CommonData> levelWrapper = commonDataQueryWrapperLevel.eq("name", readBook.getLevelName());

            List<CommonData> systemList = commonDataService.list(systemWrapper);
            List<CommonData> hierarchyList = commonDataService.list(hierarchyWrapper);
            List<CommonData> levelList = commonDataService.list(levelWrapper);
            commonDataQueryWrapper.eq("name",commonType);
            List<CommonData> commonDataList = commonDataService.list(commonDataQueryWrapper);
            if(commonDataList.size()>0){
                organization.setCommonTypeId(commonDataList.get(0).getId());
            }
            //
            if(systemList.size()>0){
                organization.setSystemId(systemList.get(0).getId());
            }
            if(hierarchyList.size()>0){
                organization.setHierarchyId(hierarchyList.get(0).getId());
            }
            if(levelList.size()>0){
                organization.setLevelId(levelList.get(0).getId());
            }
            String areaName = readBook.getAreaName();
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.eq("name",areaName);
            List<Area> list = areaService.list(areaQueryWrapper);
            if(list.size()>0){
                Area area = list.get(0);
                organization.setAreaId(area.getId());
                ArrayList<Long> areaIdList = new ArrayList<>();
                areaIdList.add(area.getId());
                int max=10;
                int i=0;
                while (area.getParentId()!=0&&i<max){
                    i++;
                    area=areaService.getById(area.getParentId());
                    areaIdList.add(area.getId());
                }
                Collections.reverse(areaIdList);
                String areaIdString = JSON.toJSONString(areaIdList);
                organization.setAreaIdArray(areaIdString);
            }
            result.add(organization);
        }
        if(result.size()>0){
            saveBatch(result);
        }
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
                List<Contacts> contactsArrayList;
                ArrayList<ContactsBO> contactsBOS = new ArrayList<>();
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
                    for (Contacts contact : contactsArrayList) {
                        ContactsBO contactsBO = new ContactsBO();
                        BeanUtil.copyProperties(contact,contactsBO);
                        contactsBO.setSeq(map.get(contact.getId()));
                        contactsBOS.add(contactsBO);
                    }
                }

                organizationModuleBO.setContacts(contactsBOS);
            }
            organizationModuleBOS.add(organizationModuleBO);
        }
        organizationVO.setOrganizationModules(organizationModuleBOS);
    }
}
