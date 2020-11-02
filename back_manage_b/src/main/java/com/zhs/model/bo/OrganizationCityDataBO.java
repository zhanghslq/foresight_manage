package com.zhs.model.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
}
