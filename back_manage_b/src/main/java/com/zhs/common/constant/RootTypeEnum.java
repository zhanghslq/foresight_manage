package com.zhs.common.constant;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: zhs
 * @date: 2020/7/11 15:49
 */
@Getter
@AllArgsConstructor
public enum  RootTypeEnum {
    PARTY(1,"党务"),
    GOVERNMENT(2,"政务"),
    LEGAL(3,"法务"),
    POLITICAL_PARTICIPATION(4,"参政"),
    MILITARY(5,"军事"),
    COMPANY(6,"企业");
    private final Integer id;
    private final String name;

    public static String getNameById(Integer type) {
        for (RootTypeEnum value : RootTypeEnum.values()) {
            if(value.id.equals(type)){
                return value.name;
            }
        }

        return "";
    }
}
