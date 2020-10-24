package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.ASMSerializerFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.constant.DownBoxTypeEnum;
import com.zhs.common.constant.DropDownBoxTypeEnum;
import com.zhs.common.constant.ModuleTypeEnum;
import com.zhs.common.constant.RootTypeEnum;
import com.zhs.exception.MyException;
import com.zhs.mapper.CompanyMapper;
import com.zhs.model.bo.CompanyModuleBO;
import com.zhs.model.bo.ContactsBO;
import com.zhs.model.bo.OrganizationHasParentBO;
import com.zhs.model.bo.OrganizationTagBO;
import com.zhs.model.dto.CompanyImportConvertDTO;
import com.zhs.model.vo.CompanyVO;
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

    @Autowired
    private OrganizationTagService organizationTagService;

    @Autowired
    private ModuleContactsService moduleContactsService;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private AreaService areaService;



    @Autowired
    private DownBoxDataService downBoxDataService;

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

    @Override
    public void dealTags(Long id, List<OrganizationTagBO> tags) {
        if(Objects.isNull(tags)||tags.size()==0){
            QueryWrapper<OrganizationTag> organizationTagQueryWrapper = new QueryWrapper<>();
            organizationTagQueryWrapper.eq("organization_id",id);
            organizationTagQueryWrapper.eq("is_company",1);
            organizationTagService.remove(organizationTagQueryWrapper);
            return;
        }
        QueryWrapper<OrganizationTag> organizationTagQueryWrapper = new QueryWrapper<>();
        organizationTagQueryWrapper.eq("organization_id",id);
        organizationTagQueryWrapper.eq("is_company",1);
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
            organizationTag.setIsCompany(1);
            organizationTag.setOrganizationId(id);
            organizationTagArrayList.add(organizationTag);
        }
        organizationTagService.saveOrUpdateBatch(organizationTagArrayList);
    }

    @Override
    public OrganizationHasParentBO listParentById(Long organizationId) {
        OrganizationHasParentBO organizationHasParentBO = companyMapper.listParentById(organizationId);

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
        Assert.notNull(organizationModule,"下属企业模块不能为空");
        Integer isCompany = organizationModule.getIsCompany();
        if(Objects.isNull(isCompany)||isCompany!=1){
            throw new MyException("模块不是企业模块");
        }

        Company companyById = getById(organizationModule.getOrganizationId());
        Assert.notNull(companyById,"企业不存在");
        List<CompanyImportConvertDTO> readBooks = EasyExcelUtil.readListFrom(fileInputStream, CompanyImportConvertDTO.class);
        excelFile.delete();
        List<Company> result = new ArrayList<>();
        // 解析Excel
        for (CompanyImportConvertDTO readBook : readBooks) {
            Company company = new Company();
            company.setModuleId(moduleId);
            company.setParentId(organizationModule.getOrganizationId());
            Subject subject = SecurityUtils.getSubject();
            company.setAdminId(Long.valueOf(subject.getPrincipal().toString()));
            company.setAddressDetail(readBook.getAddressDetail());
            company.setPerfectValue(readBook.getPerfectValue());
            company.setSeq(readBook.getSeq());
            company.setName(readBook.getName());
            company.setCompanyTypeName(readBook.getCompanyTypeName());
            company.setCompanyLevelName(readBook.getLevelName());
            company.setIsMarket(readBook.getIsMarket());
            company.setMarkedCode(readBook.getMarkedCode());
            company.setWebsite(readBook.getWebsite());
            QueryWrapper<DownBoxData> commonDataQueryWrapperLevel = new QueryWrapper<>();
            QueryWrapper<DownBoxData> commonDataQueryWrapperType = new QueryWrapper<>();
            QueryWrapper<DownBoxData> commonDataQueryWrapperRelationType = new QueryWrapper<>();
            QueryWrapper<DownBoxData> commonDataQueryWrapperMarket = new QueryWrapper<>();

            commonDataQueryWrapperLevel.eq("name", readBook.getLevelName());
            commonDataQueryWrapperLevel.eq("type", DownBoxTypeEnum.ORGANIZATION_LEVEL.getId());

            commonDataQueryWrapperType.eq("type",DropDownBoxTypeEnum.COMPANY_TYPE.getId());
            commonDataQueryWrapperType.eq("name", readBook.getCompanyTypeName());

            commonDataQueryWrapperRelationType.eq("type",DownBoxTypeEnum.COMPANY_RELATIONSHIP_TYPE.getId());
            commonDataQueryWrapperRelationType.eq("name",readBook.getRelationshipType());

            commonDataQueryWrapperMarket.eq("type",DownBoxTypeEnum.COMPANY_MARKET_SITUATION.getId());
            commonDataQueryWrapperMarket.eq("name",readBook.getMarketTypeName());


            List<DownBoxData> levelList = downBoxDataService.list(commonDataQueryWrapperLevel);
            List<DownBoxData> typeList = downBoxDataService.list(commonDataQueryWrapperType);
            List<DownBoxData> relationTypeList = downBoxDataService.list(commonDataQueryWrapperRelationType);
            List<DownBoxData> marketTypeList = downBoxDataService.list(commonDataQueryWrapperMarket);

            if(levelList.size()>0){
                company.setCompanyLevelId(levelList.get(0).getId().longValue());
            }
            if(typeList.size()>0){
                company.setCompanyTypeId(typeList.get(0).getId().longValue());
            }
            if(relationTypeList.size()>0){
                company.setRelationshipTypeId(relationTypeList.get(0).getId().longValue());
            }
            if(marketTypeList.size()>0){
                company.setMarketTypeId(marketTypeList.get(0).getId().longValue());
            }

            String areaName = readBook.getAreaName();
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.eq("name",areaName);
            List<Area> list = areaService.list(areaQueryWrapper);
            if(list.size()>0){
                Area area = list.get(0);
                company.setAreaId(area.getId());
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
                company.setAreaIdArray(areaIdString);
            }
            result.add(company);
        }
        if(result.size()>0){
            saveBatch(result);
        }
    }

    @Override
    public List<Company> listByOrganizationType(Long organizationTypeId, Long areaId) {
        Assert.notNull(organizationTypeId,"类别不允许为空");
        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.eq(Objects.nonNull(areaId),"area_id",areaId);
        companyQueryWrapper.eq("organization_type_id",organizationTypeId);
        return list(companyQueryWrapper);
    }

    private void getContactAndLeader(CompanyVO companyVO, Long organizationId) {
        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.eq("parent_id",organizationId);
        // 查到的子类组织
        List<Company> companyChildren = list(companyQueryWrapper);

        Map<Long, List<Company>> companyMap = companyChildren.stream().collect(Collectors.groupingBy(item -> {
            if (item.getModuleId() == null) {
                return 0L;
            }
            return item.getModuleId();
        }));

        QueryWrapper<OrganizationTag> organizationTagQueryWrapper = new QueryWrapper<>();
        organizationTagQueryWrapper.eq("organization_id",organizationId);
        organizationTagQueryWrapper.eq("is_company",1);

        List<OrganizationTag> list = organizationTagService.list(organizationTagQueryWrapper);
        if(list.size()>0){
            ArrayList<OrganizationTagBO> organizationTagBOS = new ArrayList<>();
            for (OrganizationTag organizationTag : list) {
                OrganizationTagBO organizationTagBO = new OrganizationTagBO();
                BeanUtil.copyProperties(organizationTag,organizationTagBO);
                organizationTagBOS.add(organizationTagBO);
            }
            companyVO.setTags(organizationTagBOS);
        }


        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("organization_id", organizationId);
        leaderQueryWrapper.eq("type", RootTypeEnum.COMPANY.getId());
        List<Leader> leaderList = leaderService.list(leaderQueryWrapper);

        Map<Long, List<Leader>> leaderMap = leaderList.stream().collect(Collectors.groupingBy(item -> {
            if (item.getModuleId() == null) {
                return 0L;
            }
            return item.getModuleId();
        }));

        QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
        organizationModuleQueryWrapper.eq("organization_id",organizationId);
        organizationModuleQueryWrapper.eq("is_company",1);
        organizationModuleQueryWrapper.orderByAsc("seq");
        List<OrganizationModule> organizationModuleList = organizationModuleService.list(organizationModuleQueryWrapper);
        List<CompanyModuleBO> companyModuleBOS = new ArrayList<>();
        for (OrganizationModule organizationModule : organizationModuleList) {
            CompanyModuleBO companyModuleBO = new CompanyModuleBO();
            companyModuleBO.setModuleName(organizationModule.getName());
            companyModuleBO.setModuleId(organizationModule.getId());
            if(ModuleTypeEnum.LEADER.getId().equals(organizationModule.getType())){
                companyModuleBO.setType(ModuleTypeEnum.LEADER.getId());
                List<Leader> leaders = leaderMap.get(organizationModule.getId());
                if(!Objects.isNull(leaders)){
                    companyModuleBO.setLeaders(leaders.stream().sorted(Comparator.comparingInt(Leader::getSeq)).collect(Collectors.toList()));
                }
            }else if(ModuleTypeEnum.CONTACTS.getId().equals(organizationModule.getType())){
                companyModuleBO.setType(ModuleTypeEnum.CONTACTS.getId());

                // 可以直接根据moduleId进行查询
                QueryWrapper<ModuleContacts> moduleContactsQueryWrapper = new QueryWrapper<>();
                moduleContactsQueryWrapper.eq("module_id",organizationModule.getId());

                List<ModuleContacts> moduleContactsList = moduleContactsService.list(moduleContactsQueryWrapper);
                // 根据模块联系人查
                List<Contacts> contactsArrayList = new ArrayList<>();
                ArrayList<ContactsBO> contactsBOS = new ArrayList<>();
                if(moduleContactsList.size()>0){
                    Map<Long, Integer> map = moduleContactsList.stream().collect(Collectors.toMap(ModuleContacts::getContactId, ModuleContacts::getSeq));
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
                companyModuleBO.setContacts(contactsBOS);
            }else if(ModuleTypeEnum.COMPANY_CHILDREN.getId().equals(organizationModule.getType())){
                companyModuleBO.setType(ModuleTypeEnum.COMPANY_CHILDREN.getId());
                List<Company> companies = companyMap.get(organizationModule.getId());
                if(!Objects.isNull(companies)){
                    companyModuleBO.setCompanyChildren(companies.stream().sorted(Comparator.comparingInt(Company::getSeq)).collect(Collectors.toList()));

                }
            }
            companyModuleBOS.add(companyModuleBO);
        }
        companyVO.setCompanyModules(companyModuleBOS);
    }
}
