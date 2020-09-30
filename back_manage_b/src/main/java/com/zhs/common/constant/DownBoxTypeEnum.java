package com.zhs.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: zhs
 * @since: 2020/9/30 14:57
 */
@Getter
@AllArgsConstructor
public enum DownBoxTypeEnum {
    LEADER_LEVEL(1,"领导人级别"),
    ORGANIZATION_LEVEL(2,"行政级别"),
    SYSTEM_SERIES(3,"所属体系"),
    ADMINISTRATION_SYSTEM(4,"所属系统"),
    ORGANIZATION_TYPE(5,"单位类型"),
    COMPANY_TYPE(6,"企业类别"),
    COMPANY_RELATIONSHIP_TYPE(7,"上下级关系"),
    COMPANY_MARKET_SITUATION(8,"上市情况"),
    NATION(9,"民族"),
    PARTIES(10,"党派"),
    RESUME_STATUS(11,"简历状态"),
    RESUME_RELATION(12,"特殊关联"),
    EXPERT_CLASSIFICATION(13,"专家分类"),
    EXPERT_LEVEL(14,"专家级别"),
    EXPERT_FIELD(15,"从事领域");

    private final Integer id;
    private final String name;
}
