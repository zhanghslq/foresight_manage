package com.zhs.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统公共配置的类型
 * @author: zhs
 * @date: 2020/7/11 13:02
 */
@Getter
@AllArgsConstructor
public enum CommonTypeEnum {
    ADMINISTRATIVE_LEVEL(1,"行政级别"),
    BELONG_SYSTEM(2,"所属体系"),
    BELONG_SYSTEM_DETAIL(3,"所属系统"),
    COMPANY_TYPE(4,"单位类型");
    private final Integer type;
    private final String name;




}
