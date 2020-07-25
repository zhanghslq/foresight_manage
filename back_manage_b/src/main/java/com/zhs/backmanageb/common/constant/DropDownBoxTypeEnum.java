package com.zhs.backmanageb.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 用于下拉框的取值区分
 * @author: zhs
 * @date: 2020/7/12 8:33
 */

@Getter
@AllArgsConstructor
@ToString
public enum DropDownBoxTypeEnum {
    CONCAT_LEVEL(1,"联系人行政级别"),
    EXPERT_LEVEL(2,"专家行政级别"),
    EXPERT_FIELD(3,"专家所属领域"),
    ORGANIZATION_LEVEL(4,"组织的行政级别"),
    ORGANIZATION_TYPE(5,"机构所属类型"),
    PARTIES(6,"党派"),
    NATION(7,"民族"),
    SYSTEM_SERIES(8,"机构体系"),
    SYSTEM(9,"机构系统"),
    COMPANY_LEVEL(10,"企业级别"),
    COMPANY_TYPE(11,"企业类别"),
    COMPANY_RELATIONSHIP_TYPE(12,"企业关系类型"),
    MARKET_SITUATION(13,"公司上市情况");


    private final Integer id;
    private final String name;
}
