package com.lergo.framework.exception;

public class SysException extends RuntimeException {

    public SysException(String message) {
        super(message);
    }

    public SysException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
