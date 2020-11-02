package com.zhs.model.bo;

import lombok.Data;

/**
 * 地区的简单数据
 * @author: zhs
 * @since: 2020/11/2 16:03
 */
@Data
public class OrganizationCityDataBO {
    private String cityName;
    private Integer contactCount;
    private Integer leaderCount;
}
