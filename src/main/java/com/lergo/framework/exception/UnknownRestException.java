package com.lergo.framework.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

@Slf4j
@RestControllerAdvice
public class UnknownRestException {

//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public String unknownExceptionHandler(Exception e) {
//        log.error("Unknown ERROR |", e);
//        return e.getMessage();
//    }

    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public String unsupportedMediaTypeExceptionHandler(UnsupportedMediaTypeStatusException e) {
        log.error("{} <-- {}", e.getSupportedMediaTypes(), e.getContentType());
        return e.getMessage();
    }
}
