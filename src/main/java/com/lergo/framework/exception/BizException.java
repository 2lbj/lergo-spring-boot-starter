package com.lergo.framework.exception;

public class BizException extends SysException {

    public Integer code = 500;

    public BizException(String message) {
        super(message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BizException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

}
