package com.zhs.common.common;

import java.util.*;

/**
 * @author EDZ
 */

public enum ReturnCode {
    //通用错误码 1-1000
    SUCCESS(0, "成功"),
    INVALID_SIGN(1, "非法请求"),
    PARAM_TYPE_ERROR(4, "参数类型错误"),
    PARAM_ERROR(5, "参数错误"),
    REDIS_ERROR(6, "REDIS错误"),
    SMS_SEND_ERROR(7, "短信发送失败，请稍后再试"),
    QINIU_STORAGE_ERROR(8, "存储失败，请稍后再试"),
    SMS_CODE_ERROR(9, "验证码错误或失效"),
    NO_RIGHT(10, "没有权限或非法操作"),
    EMAIL_FORMAT_ERROR(11, "邮箱格式不正确"),
    EMAIL_SEND_ERROR(12, "邮箱发送失败，请稍后再试"),
    //用户模块 1001-2000
    INVALID_TOKEN(1001, "登录过期，请重新登录"),
    TOKEN_NOT_MATCH(1002, "请重新登录"),
    USER_NOT_EXIST(1003, "请检查用户名是否正确"),
    USER_PASSWORD_WRONG(1004, "用户名和密码不匹配"),
    OPERATE_NOT_SAFE(1005, "不安全的操作"),
    TEACHER_NOT_HAS_THIS_STUDENT(1006, "老师没有这个学生"),
    MOBILE_EXIST(1007, "手机号已存在"),
    OLD_PASSWORD_WRONG(1008, "原密码输入错误"),
    INVALID_TIMESTAMP(1009, "错误的时间戳，请调整系统时间"),
    USER_NO_AUTHORITY(1010, "抱歉，您没有权限"),
    EXPIRED_TOKEN(1011, "登录过期或无效，请您重新登录"),
    MOBILE_USER_NOT_EXIST(1012, "该手机号码没有绑定账户"),
    MOBILE_USER_HAS_EXIST(1013, "该手机号码已经绑定了其他账户"),
    MOBILE_FORMAT_ERROR(1015, "手机号格式不正确"),
    PASSWORD_IS_EMPTE(1016, "密码输入不能为空"),
    CHECKKEY_IS_WRONG(1017, "验证码输入有误"),
    WX_IS_ALERADY_BINDING(1018, "微信已绑定其它账号!"),

    //典型正误模块
    TYPICAL_ANSWER_EXIT(7001, "该作答已经添加过典型正误"),

    //点赞分享
    ANSWERID_NOT_EXIST(26101, "答案ID不能为空"),
    QUESTIONID_NOT_EXIST(26201, "问题ID不能为空"),
    TASKID_NOT_EXIST(26301, "作业ID不能为空"),
    //json解析有问题
    JSON_SYNTAX_EXCEPTION(216101, "json格式有问题"),
    JSON_IO_EXCEPTION(216201, "json数据转换异常"),


    // 通用内部错误，不细述错误原因
    INNER_ERROR(120006, "内部错误"),

    // 身份认证与权限认证
    AUTHENTICATION_FAIL(130001, "身份认证失败"),
    AUTHORIZATION_FAIL(130002, "权限认证失败");


    private static final Map<Integer, ReturnCode> CODE_MAP;
    static {
        CODE_MAP = new HashMap<>();
        for (ReturnCode item : ReturnCode.values()) {
            CODE_MAP.put(item.getCode(), item);
        }
    }

    private int code;

    private String desc;

    ReturnCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "[" + this.code + "]" + this.desc;
    }

    public boolean equals(int code) {
        ReturnCode returnCode = CODE_MAP.get(code);
        if (returnCode == null) {
            return false;
        } else {
            return this.getCode() == code;
        }
    }
}
