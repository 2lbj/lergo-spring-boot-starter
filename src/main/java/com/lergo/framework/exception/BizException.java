package com.lergo.framework.exception;

public class BizException extends RuntimeException {

    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
