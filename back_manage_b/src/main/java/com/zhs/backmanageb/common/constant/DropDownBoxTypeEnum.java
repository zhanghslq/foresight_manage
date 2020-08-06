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
    // ORGANIZATION_LEVEL(4,"组织的行政级别"),
    // ORGANIZATION_TYPE(5,"机构所属类型"),
    PARTIES(6,"党派"),
    NATION(7,"民族"),
    //SYSTEM_SERIES(8,"机构体系"),
   // SYSTEM(9,"机构系统"),
    COMPANY_LEVEL(10,"企业行政级别"),
    COMPANY_TYPE(11,"企业类别"),
    COMPANY_RELATIONSHIP_TYPE(12,"企业关系类型"),
    MARKET_SITUATION(13,"公司上市情况"),

    ARMY_ORGANIZATION_LEVEL(14,"军事机构行政级别"),
    GOVERNMENT_AFFAIRS_ORGANIZATION_LEVEL(15,"政务机构行政级别"),
    LEGAL_AFFAIRS_ORGANIZATION_LEVEL(16,"法务机构行政级别"),
    POLITICAL_PARTICIPATION_ORGANIZATION_LEVEL(17,"参政机构行政级别"),
    PARTY_AFFAIRS_ORGANIZATION_LEVEL(18,"党务机构行政级别"),

    ARMY_SYSTEM_SERIES(19,"军事机构体系"),
    ARMY_SYSTEM(20,"军事机构系统"),
    GOVERNMENT_AFFAIRS_SYSTEM_SERIES(21,"政务机构体系"),
    GOVERNMENT_AFFAIRS_SYSTEM(22,"政务机构系统"),
    LEGAL_AFFAIRS_SYSTEM_SERIES(23,"法务机构体系"),
    LEGAL_AFFAIRS_SYSTEM(24,"法务机构系统"),
    POLITICAL_PARTICIPATION_SYSTEM_SERIES(25,"参政机构体系"),
    POLITICAL_PARTICIPATION_SYSTEM(26,"参政机构系统"),
    PARTY_AFFAIRS_SYSTEM_SERIES(27,"党务机构体系"),
    PARTY_AFFAIRS_SYSTEM(28,"党务机构系统"),

    ARMY_ORGANIZATION_TYPE(29,"军事机构所属类型"),
    GOVERNMENT_AFFAIRS_ORGANIZATION_TYPE(30,"政务机构所属类型"),
    LEGAL_AFFAIRS_ORGANIZATION_TYPE(31,"法务机构所属类型"),
    POLITICAL_PARTICIPATION_ORGANIZATION_TYPE(32,"参政机构所属类型"),
    PARTY_AFFAIRS_ORGANIZATION_TYPE(33,"党务机构所属类型"),

    LEADER_LEVEL(34,"领导人行政级别"),
    RESUME_LEVEL(35,"简历行政级别"),
    RESUME_STATUS(36,"简历状态");



    private final Integer id;
    private final String name;
}
