package com.zhs.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.constant.RootTypeEnum;
import com.zhs.entity.Organization;
import com.zhs.entity.OrganizationType;
import com.zhs.entity.RootType;
import com.zhs.exception.MyException;
import com.zhs.mapper.OrganizationTypeMapper;
import com.zhs.model.bo.OrganizationTypeBO;
import com.zhs.model.vo.CommonTypeVO;
import com.zhs.model.vo.RootTypeVO;
import com.zhs.service.OrganizationService;
import com.zhs.service.OrganizationTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhs.service.RootTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-07-11
 */
@Service
public class OrganizationTypeServiceImpl extends ServiceImpl<OrganizationTypeMapper, OrganizationType> implements OrganizationTypeService {
    @Autowired
    private OrganizationTypeMapper organizationTypeMapper;

    @Autowired
    private RootTypeService rootTypeService;

    @Autowired
    private OrganizationService organizationService;

    @Override
    public List<CommonTypeVO> listType() {
        List<CommonTypeVO> result = new ArrayList<>();
        List<RootType> rootTypeList = rootTypeService.list();
        for (RootType rootType : rootTypeList) {
            CommonTypeVO commonTypeVO = new CommonTypeVO();
            commonTypeVO.setId(Long.valueOf(rootType.getId())).setName(rootType.getName());
            result.add(commonTypeVO);
        }
        return result;
    }

    @Override
    public List<OrganizationTypeBO> listAll() {
        List<CommonTypeVO> commonTypeVOS = listType();
        List<OrganizationTypeBO> result = new ArrayList<>();

        // 查所有带子类的,parentId不为0的
        List<Long> parentIds = organizationTypeMapper.selectAllIdHasChild();

        List<OrganizationTypeBO> hasChild;
        if(parentIds.size()!=0){

            hasChild = organizationTypeMapper.selectAllHasChild(parentIds);
        }else {
            hasChild=new ArrayList<>();
        }

        // 最顶级的，parentId为0 的

        List<OrganizationTypeBO> firstList =organizationTypeMapper.listByParentIdZero();

        Map<Integer, List<OrganizationTypeBO>> map = firstList.stream().collect(Collectors.groupingBy(OrganizationTypeBO::getType));
        for (CommonTypeVO commonTypeVO : commonTypeVOS) {
            OrganizationTypeBO organizationTypeBO = new OrganizationTypeBO();
            organizationTypeBO.setId(commonTypeVO.getId());
            organizationTypeBO.setName(commonTypeVO.getName());
            organizationTypeBO.setChildren(map.get(commonTypeVO.getId().intValue()));
            result.add(organizationTypeBO);
        }
        result.addAll(hasChild);
        return result;
    }

    @Override
    public List<RootTypeVO> listAllTree() {
        List<CommonTypeVO> commonTypeVOS = listType();
        List<RootTypeVO> result = new ArrayList<>();
        for (CommonTypeVO commonTypeVO : commonTypeVOS) {
            RootTypeVO rootTypeVO = new RootTypeVO();
            BeanUtil.copyProperties(commonTypeVO,rootTypeVO);
            result.add(rootTypeVO);
        }
        List<OrganizationTypeBO> organizationTypeBOS = organizationTypeMapper.selectAllTree(null);
        Map<Integer, List<OrganizationTypeBO>> map = organizationTypeBOS.stream().collect(Collectors.groupingBy(OrganizationTypeBO::getType));
        for (RootTypeVO rootTypeVO : result) {
            rootTypeVO.setChildren(map.get(rootTypeVO.getId().intValue()));
        }
        return result;
    }

    @Override
    public void insertBatch(String content, Integer type, Long parentId, Integer hasLocation) {
        ArrayList<OrganizationType> organizationTypeArrayList = new ArrayList<>();
        if(StringUtils.isEmpty(content)){
            throw new MyException("内容不能为空");
        }
        String[] names = content.split("\n");
        for (String name : names) {
            OrganizationType organizationType = new OrganizationType();
            organizationType.setName(name.trim());
            organizationType.setType(type);
            organizationType.setParentId(parentId);
            organizationType.setHasLocation(hasLocation);
            organizationTypeArrayList.add(organizationType);
        }
        saveBatch(organizationTypeArrayList);
    }

    @Override
    public List<OrganizationTypeBO> listAllTreeByType(Integer type) {
        return organizationTypeMapper.selectAllTree(type);
    }

    @Override
    public List<Organization> listSonOrganization(Long organizationTypeId) {
        QueryWrapper<OrganizationType> organizationTypeQueryWrapper = new QueryWrapper<>();
        organizationTypeQueryWrapper.eq("parent_id",organizationTypeId);
        List<OrganizationType> list = list(organizationTypeQueryWrapper);
        if(list.size()>0){
            List<Long> organizationTypeList = list.stream().map(OrganizationType::getId).collect(Collectors.toList());
            QueryWrapper<Organization> organizationQueryWrapper = new QueryWrapper<>();
            organizationQueryWrapper.eq("parent_id",0);
            organizationQueryWrapper.in("organization_type_id",organizationTypeList);
            return organizationService.list(organizationQueryWrapper);
        }
        return Collections.emptyList();
    }
}
