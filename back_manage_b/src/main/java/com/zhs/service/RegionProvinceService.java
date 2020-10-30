package com.zhs.service;

import com.zhs.entity.Area;
import com.zhs.entity.RegionProvince;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zhs
 * @since 2020-10-30
 */
public interface RegionProvinceService extends IService<RegionProvince> {

    List<Area> listByRegionId(Integer regionId);
}
