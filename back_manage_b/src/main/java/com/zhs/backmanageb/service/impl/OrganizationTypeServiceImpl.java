package com.zhs.backmanageb.service.impl;

import com.zhs.backmanageb.common.constant.CommonTypeEnum;
import com.zhs.backmanageb.common.constant.RootTypeEnum;
import com.zhs.backmanageb.entity.OrganizationType;
import com.zhs.backmanageb.mapper.OrganizationTypeMapper;
import com.zhs.backmanageb.model.vo.CommonTypeVO;
import com.zhs.backmanageb.service.OrganizationTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
    public List<CommonTypeVO> listAll() {
        List<CommonTypeVO> commonTypeVOS = listType();
        List<OrganizationType> list = list();
        Map<Integer, List<OrganizationType>> map = list.stream().collect(Collectors.groupingBy(OrganizationType::getType));
        for (CommonTypeVO commonTypeVO : commonTypeVOS) {
            commonTypeVO.setOrganizationTypeList(map.get(commonTypeVO.getId().intValue()));
        }
        return commonTypeVOS;
    }
}
