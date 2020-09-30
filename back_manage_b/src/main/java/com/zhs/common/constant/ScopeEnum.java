package com.zhs.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: zhs
 * @since: 2020/9/30 14:58
 */
@Getter
@AllArgsConstructor
public enum ScopeEnum {
    PARTY(1,"党务"),
    GOVERNMENT(2,"政务"),
    LEGAL(3,"法务"),
    POLITICAL_PARTICIPATION(4,"参政"),
    ARMY(5,"军事"),
    COMPANY(6,"企业"),
    RESUME(7,"人事"),
    EXPERT(8,"专家"),
    CONTACTS(9,"联系人");
    private final Integer id;
    private final String name;
}
