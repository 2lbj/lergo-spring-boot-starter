package com.lergo.framework.exception;

import com.lergo.framework.entity.BizErrorEnum;

public class BizException extends SysException {

    Integer code = 500;

    public BizException(String message) {
        super(message);
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(BizErrorEnum bizErrorEnum) {
        super(bizErrorEnum.getMessage());
        this.code = bizErrorEnum.getCode();
    }

    public BizException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public BizException(int code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public BizException(BizErrorEnum bizErrorEnum, Throwable throwable) {
        super(bizErrorEnum.getMessage(), throwable);
        this.code = bizErrorEnum.getCode();
    }

}
