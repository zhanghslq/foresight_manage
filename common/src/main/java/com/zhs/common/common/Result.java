package com.zhs.common.common;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -5876274825677709598L;

    private int code;

    private T data;

    private String desc;

    private String token;

    public static <T> Result<T> fail(int code, String desc, T flag) {
        Result<T> result = new Result<>();
        result.code = code;
        result.desc = desc;
        result.data = flag;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.desc = ReturnCode.SUCCESS.getDesc();
        result.data = object;
        result.code = ReturnCode.SUCCESS.getCode();
        return result;
    }
    public static <T> Result<T> success(T object,String token) {
        Result<T> result = new Result<>();
        result.desc = ReturnCode.SUCCESS.getDesc();
        result.data = object;
        result.code = ReturnCode.SUCCESS.getCode();
        result.token = token;
        return result;
    }

    public static <T> Result<T> fail(ReturnCode returnCode, T flag) {
        return fail(returnCode.getCode(), returnCode.getDesc(), flag);
    }

}
