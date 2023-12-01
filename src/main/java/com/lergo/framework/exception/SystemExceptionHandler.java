package com.lergo.framework.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler {

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(RuntimeException e) {
        log.error("{} <-- {}",
                e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage(),
                e.getStackTrace(), e);
        return e.getMessage() == null ? e.toString() : e.getMessage();
    }

    /**
     * 处理请求体类型不支持的异常
     */
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public String unsupportedMediaTypeExceptionHandler(UnsupportedMediaTypeStatusException e) {
        log.error("{} <-- {}", e.getSupportedMediaTypes(), e.getContentType());
        return e.getMessage();
    }


    /**
     * 处理运行时异常
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(BizException e) {
        log.error("{} <-- {}",
                e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage(),
                e.getStackTrace());
        return e.getMessage() == null ? e.toString() : e.getMessage();
    }
}
