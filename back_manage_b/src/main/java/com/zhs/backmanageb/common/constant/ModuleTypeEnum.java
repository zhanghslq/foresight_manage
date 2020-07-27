package com.zhs.backmanageb.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author: zhs
 * @date: 2020/7/27 16:22
 */
@Getter
@AllArgsConstructor
@ToString
public enum ModuleTypeEnum {
    /**
     * 领导人
     */
    LEADER(0,"领导人"),
    ORGANIZATION_CHILDREN(1, "下属机构"),
    CONTACTS(2,"联系人"),
    COMPANY_CHILDREN(3,"下属企业");
    /**
     * id
     */
    private final Integer id;
    /**
     * 名称
     */
    private final String name;
}
