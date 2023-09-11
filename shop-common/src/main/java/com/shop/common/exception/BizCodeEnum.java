package com.shop.common.exception;

public enum BizCodeEnum {

    VALID_EXCEPTION(10001, "参数格式校验失败"),

    UNKNOWN_EXCEPTION(10000, "系统未知异常"),

    VALID_SMS_CODE_EXCEPTION(10002, "验证码获取频率太高"),

    PRODUCT_UP_EXCEPTION(11000, "商品上架异常"),

    USER_EXIST_EXCEPTION(15001, "用户名已存在"),

    PHONE_EXIST_EXCEPTION(15002, "手机号已存在"),

    LOGINACCT_PASSWORD_INVALID_EXCEPTION(15003, "账号或密码错误");

    private int code;

    private String msg;

    BizCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
