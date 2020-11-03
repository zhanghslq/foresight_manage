package com.zhs.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 地区的简单数据
 * @author: zhs
 * @since: 2020/11/2 16:03
 */
@Data
public class OrganizationCityDataBO {
    @ApiModelProperty("城市id")
    private Integer areaId;
    @ApiModelProperty("城市名")
    private String cityName;
    @ApiModelProperty("联系人数量")
    private Integer contactCount;
    @ApiModelProperty("领导人数量")
    private Integer leaderCount;

    @ApiModelProperty("下面的区级列表，如果已经是区级，此为空")
    private List<OrganizationCityDataBO> cityList;
}
