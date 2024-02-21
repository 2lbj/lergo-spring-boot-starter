package com.lergo.framework.entity;

public enum BizErrorEnum {
    UNKNOWN_ERROR(500001, "未知错误");

    private int code;
    private String message;

    BizErrorEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
