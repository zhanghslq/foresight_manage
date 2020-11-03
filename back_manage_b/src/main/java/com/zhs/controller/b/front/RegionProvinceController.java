package com.zhs.controller.b.front;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhs.common.Result;
import com.zhs.entity.Area;
import com.zhs.entity.RegionProvince;
import com.zhs.service.AreaService;
import com.zhs.service.RegionProvinceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhs
 * @since 2020-10-30
 */
@RestController
@RequestMapping("/regionProvince")
public class RegionProvinceController {
    @Resource
    private RegionProvinceService regionProvinceService;
    @Resource
    private AreaService areaService;

    @PostMapping("list/by_region_id")
    @ApiOperation(value = "根据地区id查询省份",tags = "查询")
    public Result<List<Area>> listByRegionId(Integer regionId){
        List<Area> result = regionProvinceService.listByRegionId(regionId);
        return Result.success(result);
    }
    @PostMapping("list/by_province_id")
    @ApiOperation(value = "根据省份查下面的城市",tags = "查询")
    public Result<List<Area>> listByProvinceId(Integer provinceId){
        QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
        areaQueryWrapper.eq(Objects.nonNull(provinceId),"parent_id",provinceId);
        return Result.success(areaService.list(areaQueryWrapper));
    }
    @ApiOperation(value = "根据地区获取城市集合",tags = "查询")
    @PostMapping("list_city/by_region_id")
    public Result<List<Area>> listCityByRegionId(Integer regionId){
        return Result.success(regionProvinceService.listCityByRegionId(regionId));
    }
    /*@ApiOperation(value = "根据省份数组获取城市集合,多个id逗号相隔",tags = "查询")
    @PostMapping("list_city/by_region_id")
    public Result<List<Area>> listCityByProvinceIds(List<Integer> provinceIds){
        if(provinceIds.size()>0){
            QueryWrapper<Area> areaQueryWrapper = new QueryWrapper<>();
            areaQueryWrapper.in("parent_id",provinceIds);
            List<Area> areaList = areaService.list(areaQueryWrapper);
        }
        return Result.success(regionProvinceService.listCityByRegionId());
    }*/

}

