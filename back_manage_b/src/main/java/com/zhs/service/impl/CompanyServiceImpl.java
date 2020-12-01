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
import com.zhs.model.vo.*;
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
import springfox.documentation.spring.web.json.Json;

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
    private RegionService regionService;

    @Autowired
    private RegionProvinceService regionProvinceService;

    @Autowired
    private DownBoxDataService downBoxDataService;

    @Autowired
    private OrganizationService organizationService;

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
            company.setMarketTypeName(readBook.getMarketTypeName());


            List<DownBoxData> levelList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_LEVEL.getId(),null);
            List<DownBoxData> typeList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.COMPANY_TYPE.getId(),null);
            List<DownBoxData> relationTypeList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.COMPANY_RELATIONSHIP_TYPE.getId(),null);
            List<DownBoxData> marketTypeList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.COMPANY_MARKET_SITUATION.getId(),null);

            List<DownBoxData> levelNameList = levelList.stream().filter(downBoxData -> Objects.nonNull(downBoxData.getName()) &&
                    downBoxData.getName().equals(readBook.getLevelName())).collect(Collectors.toList());

            if(levelNameList.size()>0){
                company.setCompanyLevelId(levelNameList.get(0).getId().longValue());

                List<Integer> integers = new ArrayList<>();
                integers.add(levelNameList.get(0).getId());
                Map<Integer, DownBoxData> map = levelList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(integers,map);

                company.setCompanyLevelIdArray(JSON.toJSONString(integers));
            }
            List<DownBoxData> typeNameList = typeList.stream().filter(downBoxData -> Objects.nonNull(downBoxData.getName()) &&
                    downBoxData.getName().equals(readBook.getCompanyTypeName())).collect(Collectors.toList());
            if(typeNameList.size()>0){
                company.setCompanyTypeId(typeNameList.get(0).getId().longValue());

                List<Integer> integers = new ArrayList<>();
                integers.add(typeNameList.get(0).getId());
                Map<Integer, DownBoxData> map = typeList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(integers,map);

                company.setCompanyTypeIdArray(JSON.toJSONString(integers));
            }
            List<DownBoxData> relationTypeNameList = relationTypeList.stream().filter(downBoxData -> Objects.nonNull(downBoxData.getName()) &&
                    downBoxData.getName().equals(readBook.getRelationshipType())).collect(Collectors.toList());
            if(relationTypeNameList.size()>0){
                Integer id = relationTypeNameList.get(0).getId();
                company.setRelationshipTypeId(id.longValue());
                List<Integer> integers = new ArrayList<>();
                integers.add(id);
                Map<Integer, DownBoxData> map = relationTypeList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(integers,map);

                company.setRelationshipTypeIdArray(JSON.toJSONString(integers));
            }
            List<DownBoxData> marketTypeNameList = marketTypeList.stream().filter(downBoxData -> Objects.nonNull(downBoxData.getName()) &&
                    downBoxData.getName().equals(readBook.getMarketTypeName())).collect(Collectors.toList());
            if(marketTypeNameList.size()>0){
                company.setMarketTypeId(marketTypeNameList.get(0).getId().longValue());

                List<Integer> integers = new ArrayList<>();
                integers.add(marketTypeNameList.get(0).getId());
                Map<Integer, DownBoxData> map = marketTypeList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(integers,map);

                company.setMarketTypeIdArray(JSON.toJSONString(integers));
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

    @Override
    public List<CompanyTypeVO> listByType(Long typeId) {
        List<CompanyTypeVO> companyList = new ArrayList<>();

        OrganizationType byId = organizationTypeService.getById(typeId);
        if(Objects.isNull(byId)){
            throw new MyException("类别不存在");
        }

        // 查一下类别的子类别
        QueryWrapper<OrganizationType> organizationTypeQueryWrapper = new QueryWrapper<>();
        organizationTypeQueryWrapper.eq("parent_id",typeId);

        List<OrganizationType> typeList = organizationTypeService.list(organizationTypeQueryWrapper);

        List<Long> typeIdList = typeList.stream().map(OrganizationType::getId).collect(Collectors.toList());
        if(typeIdList.size()==0){
            return companyList;
        }

        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.in("organization_type_id",typeIdList);
        List<Company> list = list(companyQueryWrapper);
        Map<Long, List<Company>> map = list.stream().collect(Collectors.groupingBy(Company::getOrganizationTypeId));

        for (OrganizationType organizationType : typeList) {
            CompanyTypeVO companyTypeVO = new CompanyTypeVO();
            Long id = organizationType.getId();
            List<Company> companyList1 = map.get(id);
            companyTypeVO.setCompanyList(companyList1);
            companyTypeVO.setCompanyTypeId(id);
            companyTypeVO.setCompanyTypeName(organizationType.getName());
            companyList.add(companyTypeVO);

        }

        return companyList;
    }
    public Integer countByType(Long typeId) {
        // 查一下类别的子类别
        QueryWrapper<OrganizationType> organizationTypeQueryWrapper = new QueryWrapper<>();
        organizationTypeQueryWrapper.eq("parent_id",typeId);
        List<OrganizationType> typeList = organizationTypeService.list(organizationTypeQueryWrapper);
        List<Long> typeIdList = typeList.stream().map(OrganizationType::getId).collect(Collectors.toList());
        typeIdList.add(typeId);

        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.in("organization_type_id",typeIdList);
        int count = count(companyQueryWrapper);
        return count;
    }
    @Override
    public List<OrganizationType> listTopOrganizationType() {
        QueryWrapper<OrganizationType> organizationTypeQueryWrapper = new QueryWrapper<>();
        organizationTypeQueryWrapper.eq("parent_id",0);
        organizationTypeQueryWrapper.eq("type",RootTypeEnum.COMPANY.getId());
        List<OrganizationType> list = organizationTypeService.list(organizationTypeQueryWrapper);
        for (OrganizationType organizationType : list) {
            Integer count = countByType(organizationType.getId());
            organizationType.setCompanyCount(count);
        }
        return list;
    }

    @Override
    public CompanyDetailVO getDetailById(Long companyId) {
        CompanyDetailVO companyDetailVO = new CompanyDetailVO();
        Company company = getById(companyId);
        Assert.notNull(company,"企业不存在");
        companyDetailVO.setId(companyId);
        companyDetailVO.setAddress(company.getAddressDetail());
        companyDetailVO.setName(company.getName());
        Long companyLevelId = company.getCompanyLevelId();
        if(Objects.nonNull(companyLevelId)){
            DownBoxData byId = downBoxDataService.getById(companyLevelId);
            if(Objects.nonNull(byId)){
                companyDetailVO.setLevelName(byId.getName());
            }
        }
        Long organizationTypeId = company.getOrganizationTypeId();
        if(Objects.nonNull(organizationTypeId)){
            DownBoxData byId = downBoxDataService.getById(organizationTypeId);
            if(Objects.nonNull(byId)){
                companyDetailVO.setTypeName(byId.getName());
            }
        }
        // 查所有的下属企业模块，里面的企业，然后按照上级类型分组
        QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
        organizationModuleQueryWrapper.eq("organization_id",companyId);
        organizationModuleQueryWrapper.eq("is_company",1);
        organizationModuleQueryWrapper.eq("type",ModuleTypeEnum.COMPANY_CHILDREN.getId());
        List<OrganizationModule> organizationModuleList = organizationModuleService.list(organizationModuleQueryWrapper);
        // 查出来下属企业模块
        if(organizationModuleList.size()>0){
            List<Long> moduleIdList = organizationModuleList.stream().map(OrganizationModule::getId).collect(Collectors.toList());
            QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
            companyQueryWrapper.in("module_id",moduleIdList);
            List<Company> companyList = list(companyQueryWrapper);
            //然后根据上级关系类型进行分组
            if(companyList.size()>0){
                List<Long> relationshipIdList = companyList.stream().filter(company1 -> Objects.nonNull(company1.getRelationshipTypeId())).map(Company::getRelationshipTypeId).collect(Collectors.toList());
                if(relationshipIdList.size()>0){
                    List<DownBoxData> downBoxDataList = downBoxDataService.listByIds(relationshipIdList);
                    Map<Long, List<Company>> relationshipCompanyMap = companyList.stream().filter(company1 -> Objects.nonNull(company1.getRelationshipTypeId())).collect(Collectors.groupingBy(Company::getRelationshipTypeId));
                    for (DownBoxData downBoxData : downBoxDataList) {
                        // 数量，然后添加到结果里面
                    }
                }
            }

        }


        return companyDetailVO;
    }

    @Override
    public List<CompanyModuleVO> listByRegionProvinceCityId(Long regionId, Long provinceId, Long cityId) {

        List<CompanyModuleVO> result = new ArrayList<>();

        List<Long> areaIdList = organizationService.getAreaIdList(regionId, provinceId, cityId);

        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.isNotNull("organization_type_id");
        if(areaIdList.size()==0){
            // 查全部
            companyQueryWrapper.isNotNull("area_id");
        }else {
            // 根据areaId查
            companyQueryWrapper.in("area_id",areaIdList);
        }
        // 地区的集合
        List<Region> regionList = regionService.list();
        // 地区和省份的关系集合
        List<RegionProvince> regionProvinceList = regionProvinceService.list();

        // 省份对应的地区
        Map<Integer, Integer> provinceRegionMap = regionProvinceList.stream().collect(Collectors.toMap(RegionProvince::getProvinceId, RegionProvince::getRegionId, (k1, k2) -> k1));

        List<Company> companyList = list(companyQueryWrapper);
        if(companyList.size()>0){
            // 查询这些企业的下属企业模板，然后对应的下属企业
            List<Long> companyIdList = companyList.stream().map(Company::getId).collect(Collectors.toList());
            QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
            organizationModuleQueryWrapper.eq("is_company",1);
            organizationModuleQueryWrapper.eq("type",ModuleTypeEnum.COMPANY_CHILDREN.getId());
            organizationModuleQueryWrapper.in("organization_id",companyIdList);
            List<OrganizationModule> moduleList = organizationModuleService.list(organizationModuleQueryWrapper);
            if(moduleList.size()>0){
                List<Long> moduleIdList = moduleList.stream().map(OrganizationModule::getId).collect(Collectors.toList());
                // 子企业
                QueryWrapper<Company> companySonQueryWrapper = new QueryWrapper<>();
                companySonQueryWrapper.in("module_id",moduleIdList);
                List<Company> list = list(companySonQueryWrapper);
                Map<Long, List<Company>> companyMap = list.stream().collect(Collectors.groupingBy(Company::getModuleId));
                for (OrganizationModule organizationModule : moduleList) {
                    CompanyModuleVO companyModuleVO = new CompanyModuleVO();
                    Long id = organizationModule.getId();
                    String name = organizationModule.getName();
                    companyModuleVO.setModuleId(id);
                    companyModuleVO.setModuleName(name);
                    List<Company> companyListThis = companyMap.get(id);
                    companyModuleVO.setCompanyList(companyListThis);
                    result.add(companyModuleVO);
                }
            }
        }
        return result;
    }

    @Override
    public CompanyTreeVO getTreeById(Long id) {
        CompanyTreeVO companyTreeVO = new CompanyTreeVO();
        Company company = getById(id);
        Assert.notNull(company,"企业不存在");
        companyTreeVO.setCompanyId(company.getId());
        companyTreeVO.setCompanyName(company.getName());
        dealSonCompany(companyTreeVO);
        return companyTreeVO;
    }

    private void dealSonCompany(CompanyTreeVO companyTreeVO) {
        Long companyId = companyTreeVO.getCompanyId();
        QueryWrapper<Company> companyQueryWrapper = new QueryWrapper<>();
        companyQueryWrapper.eq("parent_id",companyId);
        List<Company> companyList = list(companyQueryWrapper);
        if(companyList.size()==0){
            // 没有下属企业
            return;
        }
        // 有的话


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
