package com.zhs.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: zhs
 * @date: 2020/2/16 14:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReturnCode {
    private Integer code;
    private String message;

}
