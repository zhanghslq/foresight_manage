package com.zhs.backmanageb.common;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author: zhs
 * @date: 2020/2/19 20:06
 */
@Getter
public class Result<T> implements Serializable {
    private int code;

    private T data;

    private String desc;

    private Integer count;

    public static <T> Result<T> fail(int code, String desc, T flag) {
        Result<T> result = new Result<>();
        result.code = code;
        result.desc = desc;
        result.data = flag;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.code = 0;
        result.data = object;
        return result;
    }
    public static <T> Result<T> success(Integer count, T object) {
        Result<T> result = new Result<>();
        result.count = count;
        result.code = 0;
        result.data = object;
        return result;
    }

}
