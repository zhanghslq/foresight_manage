package com.zhs.model.vo;

import com.zhs.model.bo.OrganizationCityDataBO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 根据地区城市等查询得到的数据
 * @author: zhs
 * @since: 2020/11/2 15:57
 */
@Data
public class OrganizationRegionDataVO {
    @ApiModelProperty("地区id")
    private Integer regionId;
    @ApiModelProperty("地区名称")
    private String regionName;

    @ApiModelProperty("对应的地区的集合")
    private List<OrganizationCityDataBO> cityDataBOList;



}
