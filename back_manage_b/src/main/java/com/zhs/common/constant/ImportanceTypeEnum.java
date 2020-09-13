package com.zhs.common.constant;

import ch.qos.logback.core.layout.EchoLayout;
import com.sun.el.lang.ELArithmetic;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: zhs
 * @date: 2020/9/9 16:03
 */

@Getter
@AllArgsConstructor
public enum ImportanceTypeEnum {
    COMMONLY(1,"一般"),
    IMPORTANT(2,"重要"),
    CORE(3,"核心");
    private final Integer id;
    private final String name;

    public static Integer getIdByName(String name){
        if(COMMONLY.name.equals(name)){
            return COMMONLY.id;
        }else if(IMPORTANT.name.equals(name)){
            return IMPORTANT.id;
        }else if(CORE.name.equals(name)){
            return CORE.id;
        }else {
            return 0;
        }
    }


}
