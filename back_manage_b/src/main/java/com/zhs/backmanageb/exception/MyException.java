package com.zhs.backmanageb.exception;

import com.zhs.backmanageb.common.ReturnCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author: zhs
 * @date: 2020/7/11 20:12
 */
@Data
@NoArgsConstructor
@ToString
public class MyException extends RuntimeException{
    private String message;
    private Integer code ;

    public MyException(String message){
        this.code = -1;
        this.message = message;
    }
    public MyException(String message, Throwable e) {
        super(e);
        this.code = -1;
        this.message = message;
    }
}
