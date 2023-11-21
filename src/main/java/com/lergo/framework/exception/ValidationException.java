package com.lergo.framework.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolationException;
import javax.validation.Path;

@Slf4j
@RestControllerAdvice
public class ValidationException {

    //处理单个参数校验失败抛出的异常
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HashedMap<Path, String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        return e.getConstraintViolations().parallelStream()
                .peek(v -> log.warn("{} {} <-- [{}]", v.getPropertyPath(),
                        v.getMessage(), v.getInvalidValue()))
                .collect(HashedMap::new, (m, v) ->
                                m.put(v.getPropertyPath(), v.getMessage()),
                        HashedMap::putAll);
    }


    //处理 json 请求体调用接口校验失败抛出的异常
    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public HashedMap<String, String> methodArgumentNotValidExceptionHandler(WebExchangeBindException e) {
        return e.getBindingResult().getFieldErrors().parallelStream()
                .peek(v -> log.warn("{{}.{}} {} <-- [{}]", v.getObjectName(), v.getField(),
                        v.getDefaultMessage(), v.getRejectedValue()))
                .collect(HashedMap::new, (m, v) ->
                                m.put(v.getField(), v.getDefaultMessage()),
                        HashedMap::putAll);
    }

    //处理 form data方式调用接口校验失败抛出的异常

}
