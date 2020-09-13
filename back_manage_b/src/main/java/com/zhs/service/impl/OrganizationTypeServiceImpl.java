package com.zhs.service.impl;

import com.zhs.common.constant.RootTypeEnum;
import com.zhs.entity.OrganizationType;
import com.zhs.exception.MyException;
import com.zhs.mapper.OrganizationTypeMapper;
import com.zhs.model.bo.OrganizationTypeBO;
import com.zhs.model.vo.CommonTypeVO;
import com.zhs.service.OrganizationTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
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

    @Override
    public List<CommonTypeVO> listType() {
        List<CommonTypeVO> result = new ArrayList<>();
        RootTypeEnum[] values = RootTypeEnum.values();
        for (RootTypeEnum value : values) {
            CommonTypeVO commonTypeVO = new CommonTypeVO();
            commonTypeVO.setId(Long.valueOf(value.getId())).setName(value.getName());
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
    public List<CommonTypeVO> listAllTree() {
        List<CommonTypeVO> commonTypeVOS = listType();
        List<OrganizationTypeBO> organizationTypeBOS = organizationTypeMapper.selectAllTree(null);
        Map<Integer, List<OrganizationTypeBO>> map = organizationTypeBOS.stream().collect(Collectors.groupingBy(OrganizationTypeBO::getType));
        for (CommonTypeVO commonTypeVO : commonTypeVOS) {
            commonTypeVO.setOrganizationTypeListTree(map.get(commonTypeVO.getId().intValue()));
        }
        return commonTypeVOS;
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
}
