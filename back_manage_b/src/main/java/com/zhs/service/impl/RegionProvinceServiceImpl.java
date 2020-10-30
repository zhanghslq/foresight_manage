package com.zhs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.entity.Area;
import com.zhs.entity.RegionProvince;
import com.zhs.mapper.RegionProvinceMapper;
import com.zhs.service.AreaService;
import com.zhs.service.RegionProvinceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhs
 * @since 2020-10-30
 */
@Service
public class RegionProvinceServiceImpl extends ServiceImpl<RegionProvinceMapper, RegionProvince> implements RegionProvinceService {

    @Autowired
    private AreaService areaService;

    @Override
    public List<Area> listByRegionId(Integer regionId) {
        QueryWrapper<RegionProvince> regionProvinceQueryWrapper = new QueryWrapper<>();
        regionProvinceQueryWrapper.eq(Objects.nonNull(regionId),"region_id",regionId);
        List<RegionProvince> regionProvinceList = list(regionProvinceQueryWrapper);
        List<Integer> provinceIds = regionProvinceList.stream().map(RegionProvince::getProvinceId).collect(Collectors.toList());
        if(provinceIds.size()>0){
            List<Area> areaList = areaService.listByIds(provinceIds);
            return areaList;
        }
        return Collections.emptyList();
    }
}
