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
    /*CONCAT_LEVEL(1,"联系人行政级别"),
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
    RESUME_STATUS(36,"简历状态"),
    ////////////专家分类，用于专家列表的顶部分类///////////
    EXPERT_CLASSIFICATION(37,"专家分类"),
    RESUME_RELATION(38,"简历之间特殊关联");*/


    PARTY_LEADER_LEVEL(100101,1001,"党务","领导人级别"),
    PARTY_AFFAIRS_ORGANIZATION_LEVEL(100102,1001,"党务","党务机构行政级别"),
    PARTY_AFFAIRS_SYSTEM_SERIES(100103,1001,"党务","所属体系"),
    PARTY_AFFAIRS_ADMINISTRATION_SYSTEM(100104,1001,"党务","行政类系统"),
    PARTY_AFFAIRS_INDUSTRY_SYSTEM(100105,1001,"党务","产业类系统"),
    PARTY_AFFAIRS_ORGANIZATION_TYPE(100106,1001,"党务","单位类型"),

    GOVERNMENT_LEADER_LEVEL(100201,1002,"政务","领导人级别"),
    GOVERNMENT_AFFAIRS_ORGANIZATION_LEVEL(100202,1002,"政务","行政级别"),
    GOVERNMENT_AFFAIRS_SYSTEM_SERIES(100203,1002,"政务","所属体系"),
    GOVERNMENT_AFFAIRS_ADMINISTRATION_SYSTEM(100204,1002,"政务","行政类系统"),
    GOVERNMENT_AFFAIRS_INDUSTRY_SYSTEM(100205,1002,"政务","产业类系统"),
    GOVERNMENT_AFFAIRS_ORGANIZATION_TYPE(100206,1002,"政务","单位类型"),

    LEGAL_LEADER_LEVEL(100301,1003,"法务","领导人级别"),
    LEGAL_AFFAIRS_ORGANIZATION_LEVEL(100302,1003,"法务","行政级别"),
    LEGAL_AFFAIRS_SYSTEM_SERIES(100303,1003,"法务","所属体系"),
    LEGAL_AFFAIRS_ADMINISTRATION_SYSTEM(100304,1003,"法务","行政类系统"),
    LEGAL_AFFAIRS_SYSTEM(100305,1003,"法务","行政系统"),
    LEGAL_AFFAIRS_ORGANIZATION_TYPE(100306,1003,"法务","单位类型"),

    POLITICAL_LEADER_LEVEL(100401,1004,"参政","领导人级别"),
    POLITICAL_PARTICIPATION_ORGANIZATION_LEVEL(100402,1004,"参政","行政级别"),
    POLITICAL_PARTICIPATION_SYSTEM_SERIES(100403,1004,"参政","所属体系"),
    POLITICAL_PARTICIPATION_ADMINISTRATION_SYSTEM(100404,1004,"参政","行政类系统"),
    POLITICAL_PARTICIPATION_INDUSTRY_SYSTEM(100405,1004,"参政","产业类系统"),
    POLITICAL_PARTICIPATION_ORGANIZATION_TYPE(100406,1004,"参政","单位类型"),

    ARMY_LEADER_LEVEL(100501,1005,"军事","领导人级别"),
    ARMY_ORGANIZATION_LEVEL(100502,1005,"军事","行政级别"),
    ARMY_SYSTEM_SERIES(100503,1005,"军事","所属体系"),
    ARMY_ADMINISTRATION_SYSTEM(100504,1005,"军事","行政类系统"),
    ARMY_INDUSTRY_SYSTEM(100505,1005,"军事","产业类系统"),
    ARMY_ORGANIZATION_TYPE(100506,1005,"军事","单位类型"),

    COMPANY_LEADER_LEVEL(100601,1006,"企业","领导人级别"),
    COMPANY_LEVEL(100602,1006,"企业","行政级别"),
    COMPANY_TYPE(100603,1006,"企业","企业类别"),
    COMPANY_RELATIONSHIP_TYPE(100604,1006,"企业","上级关系类型"),
    COMPANY_MARKET_SITUATION(100605,1006,"企业","上市情况"),


    NATION(100701,1007,"人事","民族"),
    PARTIES(100702,1007,"人事","党派"),
    RESUME_LEVEL(100703,1007,"人事","行政级别"),
    RESUME_STATUS(100704,1007,"人事","简历状态"),
    RESUME_RELATION(100705,1007,"人事","简历之间特殊关联"),


    EXPERT_CLASSIFICATION(100801,1008,"专家","专家分类"),
    EXPERT_LEVEL(100802,1008,"专家","专家级别"),
    EXPERT_FIELD(100803,1008,"专家","从事领域"),



    CONCAT_LEVEL(100901,1009,"联系人","行政级别");

    ////////////专家分类，用于专家列表的顶部分类///////////



    private final Integer id;
    private final Integer typeId;
    private final String typeName;
    private final String name;
}
