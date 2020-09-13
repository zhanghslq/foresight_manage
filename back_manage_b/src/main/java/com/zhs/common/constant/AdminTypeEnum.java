package com.zhs.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: zhs
 * @date: 2020/9/13 14:47
 */
@Getter
@AllArgsConstructor
public enum AdminTypeEnum {
    B_BACK(0,"B系统后台管理员"),
    B_FRONT(1,"B系统前台用户");
    private final Integer type;
    private final String name;
}
