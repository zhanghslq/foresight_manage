package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xpath.internal.operations.Mod;
import com.zhs.common.constant.*;
import com.zhs.exception.MyException;
import com.zhs.mapper.OrganizationMapper;
import com.zhs.model.bo.*;
import com.zhs.model.dto.OrganizationImportConvertDTO;
import com.zhs.model.vo.OrganizationFrontVO;
import com.zhs.model.vo.OrganizationInformationVO;
import com.zhs.model.vo.OrganizationRegionDataVO;
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
    private DownBoxDataService downBoxDataService;

    @Autowired
    private AreaService areaService;

    @Autowired
    private ExperienceRecordService experienceRecordService;

    @Autowired
    private AdminOperationLogService adminOperationLogService;

    @Autowired
    private RegionProvinceService regionProvinceService;

    @Autowired
    private RegionService regionService;

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
        queryWrapper.eq("parent_id",0);
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
            organization.setWebsite(readBook.getWebsite());
            organization.setIsGovernment(readBook.getIsGovernment());
            organization.setIsAssociation(readBook.getIsAssociation());
            String isDecoupling = readBook.getIsDecoupling();
            if("已脱钩".equals(isDecoupling)){
                organization.setIsDecoupling(1);
            }else if("未脱钩".equals(isDecoupling)){
                organization.setIsDecoupling(0);
            }
            String otherName = readBook.getOtherName();
            if(Objects.nonNull(otherName)){
                String s = otherName.replaceAll("，", ",");
                String[] split = s.split(",");
                List<String> strings = Arrays.asList(split);
                organization.setOtherName(JSONArray.toJSONString(strings));
            }
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
            List<DownBoxData> systemDownBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ADMINISTRATION_SYSTEM.getId(), null);
            List<DownBoxData> systemList = systemDownBoxDataList.stream().filter(downBoxData -> !Objects.isNull(downBoxData.getName()) && downBoxData.getName().equals(readBook.getSystem())).collect(Collectors.toList());

            List<DownBoxData> hierarchyDownBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.SYSTEM_SERIES.getId(), null);

            List<DownBoxData> hierarchyList = hierarchyDownBoxDataList.stream().filter(downBoxData -> !Objects.isNull(downBoxData.getName()) && downBoxData.getName().equals(readBook.getHierarchy())).collect(Collectors.toList());

            List<DownBoxData> levelDownBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_LEVEL.getId(), null);
            List<DownBoxData> levelList = levelDownBoxDataList.stream().filter(downBoxData -> !Objects.isNull(downBoxData.getName()) && downBoxData.getName().equals(readBook.getLevelName())).collect(Collectors.toList());


            List<DownBoxData> downBoxDataList = downBoxDataService.listNoTreeByDownBoxTypeAndScope(DownBoxTypeEnum.ORGANIZATION_TYPE.getId(), null);
            List<DownBoxData> commonDataList = downBoxDataList.stream().filter(downBoxData -> !Objects.isNull(downBoxData.getName()) && downBoxData.getName().equals(commonType)).collect(Collectors.toList());


            if(commonDataList.size()>0){
                organization.setCommonTypeId(commonDataList.get(0).getId().longValue());

                List<Integer> commonTypeIds = new ArrayList<>();
                commonTypeIds.add(commonDataList.get(0).getId());
                Map<Integer, DownBoxData> collect = downBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(commonTypeIds,collect);
                Collections.reverse(commonTypeIds);
                organization.setCommonTypeIdArray(JSONArray.toJSONString(commonTypeIds));
            }
            //
            if(systemList.size()>0){
                organization.setSystemId(systemList.get(0).getId().longValue());

                List<Integer> systemIds = new ArrayList<>();
                systemIds.add(systemList.get(0).getId());
                Map<Integer, DownBoxData> collect = systemDownBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(systemIds,collect);
                Collections.reverse(systemIds);
                organization.setSystemIdArray(JSONArray.toJSONString(systemIds));
            }
            if(hierarchyList.size()>0){
                organization.setHierarchyId(hierarchyList.get(0).getId().longValue());

                List<Integer> hierarchyIds = new ArrayList<>();
                hierarchyIds.add(hierarchyList.get(0).getId());
                Map<Integer, DownBoxData> collect = hierarchyDownBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(hierarchyIds,collect);
                Collections.reverse(hierarchyIds);
                organization.setHierarchyIdArray(JSONArray.toJSONString(hierarchyIds));
            }
            if(levelList.size()>0){
                organization.setLevelId(levelList.get(0).getId().longValue());

                List<Integer> levelIds = new ArrayList<>();
                levelIds.add(levelList.get(0).getId());
                Map<Integer, DownBoxData> collect = levelDownBoxDataList.stream().collect(Collectors.toMap(DownBoxData::getId, downBoxData1 -> downBoxData1, (k1, k2) -> k1));
                ResumeServiceImpl.dealArray(levelIds,collect);
                Collections.reverse(levelIds);
                organization.setLevelIdArray(JSONArray.toJSONString(levelIds));
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
            AdminOperationLog adminOperationLog = new AdminOperationLog();
            adminOperationLog.setOperatorType("新增");
            adminOperationLog.setAdminId(result.get(0).getAdminId());
            StringBuilder sb = new StringBuilder();
            for (Organization organization : result) {
                String organizationName = organization.getName();
                if(Objects.nonNull(organizationName)){
                    sb.append(organizationName);
                    sb.append(",");
                }
            }
            adminOperationLog.setInterfaceDesc("新增机构:"+sb.toString());
            adminOperationLogService.save(adminOperationLog);
        }
    }

    @Override
    public OrganizationInformationVO queryInformationById(Long id) {
        Organization organization = getById(id);
        Assert.notNull(organization,"机构不存在");
        OrganizationInformationVO organizationInformationVO = new OrganizationInformationVO();
        organizationInformationVO.setId(organization.getId());
        organizationInformationVO.setName(organization.getName());
        organizationInformationVO.setAddressDetail(organization.getAddressDetail());
        organizationInformationVO.setWebsite(organization.getWebsite());
        organizationInformationVO.setLogoUrl(organization.getLogoUrl());
        Long levelId = organization.getLevelId();
        if(Objects.nonNull(levelId)){
            DownBoxData boxData = downBoxDataService.getById(levelId);
            if(Objects.nonNull(boxData)){
                organizationInformationVO.setLevelName(boxData.getName());
            }
        }
        Long systemId = organization.getSystemId();
        if(Objects.nonNull(systemId)){
            DownBoxData boxData = downBoxDataService.getById(systemId);
            if(Objects.nonNull(boxData)){
                organizationInformationVO.setSystemName(boxData.getName());
            }
        }
        Long hierarchyId = organization.getHierarchyId();
        if(Objects.nonNull(hierarchyId)){
            DownBoxData boxData = downBoxDataService.getById(hierarchyId);
            if(Objects.nonNull(boxData)){
                organizationInformationVO.setHierarchyName(boxData.getName());
            }
        }
        Long areaId = organization.getAreaId();
        if(Objects.nonNull(areaId)){
            Area boxData = areaService.getById(areaId);
            if(Objects.nonNull(boxData)){
                organizationInformationVO.setArea(boxData.getName());
            }
        }
        QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
        organizationModuleQueryWrapper.eq("organization_id",id);
        List<OrganizationModule> organizationModuleList = organizationModuleService.list(organizationModuleQueryWrapper);

        List<ModuleSimple> simpleArrayList = new ArrayList<>();
        for (OrganizationModule organizationModule : organizationModuleList) {
            ModuleSimple moduleSimple = new ModuleSimple();
            moduleSimple.setModuleName(organizationModule.getName());
            Integer type = organizationModule.getType();
            Long moduleId = organizationModule.getId();
            if(ModuleTypeEnum.LEADER.getId().equals(type)){
                QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
                leaderQueryWrapper.eq("module_id",moduleId);
                int count = leaderService.count(leaderQueryWrapper);
                moduleSimple.setCount(count);
            }else if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(type)){
                QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
                organizationQueryWrapper.eq("module_id",moduleId);
                int count = count(organizationQueryWrapper);
                moduleSimple.setCount(count);

            }else if(ModuleTypeEnum.CONTACTS.getId().equals(type)){
                QueryWrapper<ModuleContacts> moduleContactsQueryWrapper = new QueryWrapper<>();
                moduleContactsQueryWrapper.eq("module_id",moduleId);
                int count = moduleContactsService.count(moduleContactsQueryWrapper);
                moduleSimple.setCount(count);
            }
            simpleArrayList.add(moduleSimple);
        }
        organizationInformationVO.setModuleList(simpleArrayList);
        return organizationInformationVO;
    }

    @Override
    public OrganizationFrontVO queryFrontByOrganizationType(Long organizationTypeId, Long areaId) {
        OrganizationFrontVO organizationFrontVO = new OrganizationFrontVO();

        OrganizationVO organizationVO = queryByOrganizationType(organizationTypeId, areaId);
        Organization organization = organizationVO.getOrganization();

        organizationFrontVO.setOrganization(organization);
        if(Objects.isNull(organization)){
            return organizationFrontVO;
        }
        dealOrganizationFront(organizationFrontVO,organization);
        return organizationFrontVO;
    }

    @Override
    public OrganizationFrontVO queryFrontByParentId(Long id) {
        Organization organization = getById(id);
        Assert.notNull(organization,"机构不存在");
        OrganizationFrontVO organizationFrontVO = new OrganizationFrontVO();
        dealOrganizationFront(organizationFrontVO,organization);
        return organizationFrontVO;
    }

    @Override
    public List<OrganizationRegionDataVO> listByRegionProvinceCityId(Long regionId, Long provinceId, Long cityId) {
        List<OrganizationRegionDataVO> result = new ArrayList<>();

        List<Long> areaIdList = getAreaIdList(regionId, provinceId, cityId);

        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.isNotNull("organization_type_id");
        if(areaIdList.size()==0){
            // 查全部
            organizationQueryWrapper.isNotNull("area_id");
        }else {
            // 根据areaId查
            organizationQueryWrapper.in("area_id",areaIdList);
        }
        // 地区的集合
        List<Region> regionList = regionService.list();


        // 地区和省份的关系集合
        List<RegionProvince> regionProvinceList = regionProvinceService.list();

        // 省份对应的地区
        Map<Integer, Integer> provinceRegionMap = regionProvinceList.stream().collect(Collectors.toMap(RegionProvince::getProvinceId, RegionProvince::getRegionId, (k1, k2) -> k1));

        // 查到的机构列表
        List<Organization> organizationList = list(organizationQueryWrapper);

        List<Long> typeIdList = organizationList.stream().map(Organization::getOrganizationTypeId).collect(Collectors.toList());

        if(typeIdList.size()>0){
            List<OrganizationType> organizationTypes = organizationTypeService.listByIds(typeIdList);
            List<Long> noLocationList = organizationTypes.stream().filter(organizationType -> Objects.isNull(organizationType.getHasLocation()) || organizationType.getHasLocation() == 0)
                    .map(OrganizationType::getId).collect(Collectors.toList());
            organizationList.removeIf(organization -> noLocationList.contains(organization.getOrganizationTypeId()));
        }

        // 根据地区分组
        Map<Long, List<Organization>> areaOrganizationMap = organizationList.stream().filter(organization -> Objects.nonNull(organization.getAreaId())).collect(Collectors.groupingBy(Organization::getAreaId));

        List<Long> areaIds = organizationList.stream().map(Organization::getAreaId).distinct().collect(Collectors.toList());

        // 先把地区的放进去
        for (Region region : regionList) {
            OrganizationRegionDataVO organizationRegionDataVO = new OrganizationRegionDataVO();
            organizationRegionDataVO.setRegionId(region.getId());
            organizationRegionDataVO.setRegionName(region.getName());
            result.add(organizationRegionDataVO);
        }

        // 地区，对应的省份，
        Map<Integer, List<OrganizationCityDataBO>> provinceCountMap = new HashMap<>();
        // 省份，对应的城市
        Map<Integer, List<OrganizationCityDataBO>> cityCountMap = new HashMap<>();


        if(areaIds.size()>0){
            List<Area> areaList = areaService.listByIds(areaIds);
            Map<Long, Area> areaMap = areaList.stream().collect(Collectors.toMap(Area::getId, area -> area, (k1, k2) -> k1));

            Set<Long> areaIdSet = areaOrganizationMap.keySet();
            for (Long areaId : areaIdSet) {
                List<Organization> organizations = areaOrganizationMap.get(areaId);
                // 获取机构的领导人数量和联系人数量
                Area area = areaMap.get(areaId);
                OrganizationCityDataBO organizationCityDataBO = new OrganizationCityDataBO();
                organizationCityDataBO.setAreaId(areaId.intValue());
                if(Objects.nonNull(area)){
                    organizationCityDataBO.setCityName(area.getName());
                }else {
                    continue;
                }
                // 联系人 和领导人数量
                dealLeaderAndContactCount(organizations,organizationCityDataBO);

                Integer curRegionId = provinceRegionMap.get(areaId.intValue());
                if(Objects.nonNull(curRegionId)){
                    // 证明是省
                    dealMap(provinceCountMap, organizationCityDataBO, curRegionId);
                }else{
                    // 区或者市
                    Long parentId = area.getParentId();
                    if(Objects.nonNull(parentId)&&parentId!=0){
                        dealMap(cityCountMap, organizationCityDataBO, parentId.intValue());
                    }

                }

            }
        }
        for (OrganizationRegionDataVO organizationRegionDataVO : result) {
            Integer regionId1 = organizationRegionDataVO.getRegionId();
            List<OrganizationCityDataBO> organizationCityDataBOS = provinceCountMap.get(regionId1);
            if(Objects.nonNull(organizationCityDataBOS)){
                for (OrganizationCityDataBO organizationCityDataBO : organizationCityDataBOS) {
                    Integer areaId = organizationCityDataBO.getAreaId();
                    List<OrganizationCityDataBO> organizationCityDataBOS1 = cityCountMap.get(areaId);
                    organizationCityDataBO.setCityList(organizationCityDataBOS1);
                }
                organizationRegionDataVO.setCityDataBOList(organizationCityDataBOS);
            }
        }
        return result;
    }

    private void dealMap(Map<Integer, List<OrganizationCityDataBO>> cityCountMap, OrganizationCityDataBO organizationCityDataBO, Integer thisRegionId) {
        List<OrganizationCityDataBO> organizationCityDataBOS = cityCountMap.get(thisRegionId);
        if(Objects.isNull(organizationCityDataBOS)){
            organizationCityDataBOS = new ArrayList<>();
            cityCountMap.put(thisRegionId,organizationCityDataBOS);
        }
        organizationCityDataBOS.add(organizationCityDataBO);
    }

    private void dealLeaderAndContactCount(List<Organization> organizations, OrganizationCityDataBO organizationCityDataBO) {
        // 获取机构的联系人和领导人，然后放到后面这个类
        if(organizations.size()>0){
            // 获取机构的模块
            List<Long> organizationIdList = organizations.stream().map(Organization::getId).collect(Collectors.toList());
            QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
            organizationModuleQueryWrapper.in("organization_id",organizationIdList);
            organizationModuleQueryWrapper.in("type",ModuleTypeEnum.LEADER.getId(),ModuleTypeEnum.CONTACTS.getId());
            // 查到所有的联系人和领导模块
            List<OrganizationModule> organizationModules = organizationModuleService.list(organizationModuleQueryWrapper);
            List<OrganizationModule> leaderModuleList = organizationModules.stream().filter(organizationModule -> ModuleTypeEnum.LEADER.getId().equals(organizationModule.getType()))
                    .collect(Collectors.toList());

            List<OrganizationModule> contactModuleList = organizationModules.stream().filter(organizationModule -> ModuleTypeEnum.CONTACTS.getId().equals(organizationModule.getType()))
                    .collect(Collectors.toList());

            if(leaderModuleList.size()>0){
                List<Long> moduleIdList = leaderModuleList.stream().map(OrganizationModule::getId).collect(Collectors.toList());
                QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
                leaderQueryWrapper.in("module_id",moduleIdList);
                int count = leaderService.count(leaderQueryWrapper);
                organizationCityDataBO.setLeaderCount(count);
            }else {
                organizationCityDataBO.setLeaderCount(0);
            }

            if(contactModuleList.size()>0){
                List<Long> moduleIdList = contactModuleList.stream().map(OrganizationModule::getId).collect(Collectors.toList());
                QueryWrapper<ModuleContacts> moduleContactsQueryWrapper = new QueryWrapper<>();
                moduleContactsQueryWrapper.in("module_id",moduleIdList);
                int count = moduleContactsService.count(moduleContactsQueryWrapper);
                organizationCityDataBO.setContactCount(count);
            }else {
                organizationCityDataBO.setContactCount(0);
            }

        }

    }

    /**
     * 获取的地区id，包括省和市
     * @param regionId 地区id
     * @param provinceId 省份id
     * @param cityId 城市id
     * @return 地区和城市id
     */
    private List<Long> getAreaIdList(Long regionId, Long provinceId, Long cityId) {
        List<Long> areaIdList = new ArrayList<>();
        // 根据地区，省市，城市查机构
        if(Objects.isNull(cityId)){
            // 城市id为空的话，去查省
            if(Objects.isNull(provinceId)){
                // 省份id，为空的话去查地区
                if(Objects.nonNull(regionId)){
                    // 根据地区查
                    List<Area> areaList = regionProvinceService.listByRegionId(regionId.intValue());
                    List<Long> cityIdList = areaList.stream().map(Area::getId).collect(Collectors.toList());
                    areaIdList.addAll(cityIdList);

                    QueryWrapper<RegionProvince> regionProvinceQueryWrapper = new QueryWrapper<>();
                    regionProvinceQueryWrapper.eq("region_id",regionId);
                    List<RegionProvince> regionProvinceList = regionProvinceService.list(regionProvinceQueryWrapper);
                    List<Integer> provinceIds = regionProvinceList.stream().map(RegionProvince::getProvinceId).collect(Collectors.toList());
                    for (Integer id : provinceIds) {
                        areaIdList.add(id.longValue());
                    }
                }
            }else {
                // 省份有数据，根据省份查询

                QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
                areaQueryWrapper.eq("parent_id",provinceId);
                List<Area> list = areaService.list(areaQueryWrapper);
                List<Long> cityIds = list.stream().map(Area::getId).collect(Collectors.toList());
                areaIdList.add(provinceId);
                areaIdList.addAll(cityIds);
            }
        }else {
            // 根据城市查询
            areaIdList.add(cityId);
        }
        return areaIdList;
    }

    private void dealOrganizationFront(OrganizationFrontVO organizationFrontVO, Organization organization) {
        Long id = organization.getId();
        QueryWrapper<OrganizationModule> organizationModuleQueryWrapper = new QueryWrapper<>();
        organizationModuleQueryWrapper.eq("organization_id",id);
        String organizationName = organization.getName();
        List<OrganizationModule> organizationModuleList = organizationModuleService.list(organizationModuleQueryWrapper);

        ArrayList<ModuleFrontBO> moduleFrontBOS = new ArrayList<>();

        for (OrganizationModule organizationModule : organizationModuleList) {
            if(ModuleTypeEnum.LEADER.getId().equals(organizationModule.getType())){
                dealLeaderList(organizationFrontVO, organizationName, organizationModule);
            }else if(ModuleTypeEnum.ORGANIZATION_CHILDREN.getId().equals(organizationModule.getType())){
                ModuleFrontBO moduleFrontBO = new ModuleFrontBO();
                moduleFrontBO.setModuleName(organizationModule.getName());
                dealOrganizationModuleSon(organizationModule,moduleFrontBO);
                moduleFrontBOS.add(moduleFrontBO);
            }
        }
        organizationFrontVO.setModuleList(moduleFrontBOS);
    }

    private void dealOrganizationModuleSon(OrganizationModule organizationModule, ModuleFrontBO moduleFrontBO) {

        // 下属机构
        Long moduleId = organizationModule.getId();
        QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
        organizationQueryWrapper.eq("module_id",moduleId);
        List<Organization> list = list(organizationQueryWrapper);
        ArrayList<OrganizationSimple> organizationSimples = new ArrayList<>();
        for (Organization organizationSon : list) {
            // 各个组织的联系人,领导人数量
            OrganizationSimple organizationSimple = new OrganizationSimple();
            organizationSimple.setOrganizationId(organizationSon.getId());
            organizationSimple.setName(organizationSon.getName());
            Integer type = organizationSon.getType();
            if(Objects.nonNull(type)){
                organizationSimple.setTypeName(RootTypeEnum.getNameById(type));
            }
            organizationSimple.setImportance(organizationSon.getImportance());
            QueryWrapper<OrganizationModule> organizationModuleQueryWrapperSon = new QueryWrapper<>();
            organizationModuleQueryWrapperSon.eq("organization_id",organizationSon.getId());
            List<OrganizationModule> organizationModuleSonList = organizationModuleService.list(organizationModuleQueryWrapperSon);
            List<Long> contactModuleIdList = organizationModuleSonList.stream().filter(organizationModule1 -> ModuleTypeEnum.CONTACTS.getId().equals(organizationModule1.getType())).map(OrganizationModule::getId).collect(Collectors.toList());
            if(contactModuleIdList.size()>0){
                // 联系人数量
                QueryWrapper<ModuleContacts> moduleContactsQueryWrapper = new QueryWrapper<>();
                moduleContactsQueryWrapper.in("module_id",contactModuleIdList);
                int count = moduleContactsService.count(moduleContactsQueryWrapper);
                organizationSimple.setContactsCount(count);
            }else {
                organizationSimple.setContactsCount(0);
            }
            // 领导数量
            List<Long> leaderModuleIdList = organizationModuleSonList.stream().filter(organizationModule1 -> ModuleTypeEnum.LEADER.getId().equals(organizationModule1.getType())).map(OrganizationModule::getId).collect(Collectors.toList());
            if(leaderModuleIdList.size()>0){
                QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
                leaderQueryWrapper.in("module_id",leaderModuleIdList);
                int count = leaderService.count(leaderQueryWrapper);
                organizationSimple.setLeaderCount(count);
            }else {
                organizationSimple.setLeaderCount(0);
            }

            organizationSimples.add(organizationSimple);
        }
        moduleFrontBO.setOrganizationSimpleList(organizationSimples);
    }

    private void dealLeaderList(OrganizationFrontVO organizationFrontVO, String organizationName, OrganizationModule organizationModule) {
        // 领导人，这里区分
        Long moduleId = organizationModule.getId();
        QueryWrapper<Leader> leaderQueryWrapper = new QueryWrapper<>();
        leaderQueryWrapper.eq("module_id",moduleId);
        List<Leader> leaderList = leaderService.list(leaderQueryWrapper);
        List<Leader> hasResumeLeaderList = leaderList.stream().filter(leader -> Objects.nonNull(leader.getResumeId())).collect(Collectors.toList());
        List<Long> resumeIdList = hasResumeLeaderList.stream().map(leader -> leader.getResumeId()).collect(Collectors.toList());
        Map<Long, ExperienceRecord> map = new HashMap<>();
        if(resumeIdList.size()>0){
            QueryWrapper<ExperienceRecord> experienceRecordQueryWrapper = new QueryWrapper<>();
            experienceRecordQueryWrapper.in("resume_id",resumeIdList);
            experienceRecordQueryWrapper.eq("company_name",organizationName);
            List<ExperienceRecord> list = experienceRecordService.list(experienceRecordQueryWrapper);
            map = list.stream().collect(Collectors.toMap(ExperienceRecord::getResumeId, experienceRecord -> experienceRecord, (k1, k2) -> k2));
        }

        for (Leader leader : leaderList) {
            LeaderBO leaderBO = new LeaderBO();
            leaderBO.setName(leader.getRealName());
            Long resumeId = leader.getResumeId();
            if(Objects.nonNull(resumeId)){
                ExperienceRecord experienceRecord = map.get(resumeId);
                if(Objects.nonNull(experienceRecord)){
                    leaderBO.setStartTime(experienceRecord.getBeginDate());
                }
            }
            Long levelId = leader.getLevelId();
            if(Objects.nonNull(levelId)){
                DownBoxData level = downBoxDataService.getById(levelId);
                if(Objects.isNull(level)){
                    continue;
                }
                if(Objects.nonNull(level.getName())&&level.getName().contains("正")){
                    List<LeaderBO> justLeaderList = organizationFrontVO.getJustLeaderList();
                    if(Objects.isNull(justLeaderList)){
                        justLeaderList = new ArrayList<>();
                        organizationFrontVO.setJustLeaderList(justLeaderList);
                    }
                    justLeaderList.add(leaderBO);
                }else if(Objects.nonNull(level.getName())&&level.getName().contains("副")){
                    List<LeaderBO> viceLeaderList = organizationFrontVO.getViceLeaderList();
                    if(Objects.isNull(viceLeaderList)){
                        viceLeaderList = new ArrayList<>();
                        organizationFrontVO.setViceLeaderList(viceLeaderList);
                    }
                    viceLeaderList.add(leaderBO);
                }else {
                    List<LeaderBO> otherLeaderList = organizationFrontVO.getOtherLeaderList();
                    if(Objects.isNull(otherLeaderList)){
                        otherLeaderList = new ArrayList<>();
                        organizationFrontVO.setOtherLeaderList(otherLeaderList);
                    }
                    otherLeaderList.add(leaderBO);
                }
            }
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
