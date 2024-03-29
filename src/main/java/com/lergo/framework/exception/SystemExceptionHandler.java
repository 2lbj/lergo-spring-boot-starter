package com.lergo.framework.exception;

import com.lergo.framework.entity.CommonResult;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.util.Arrays;

@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler {

    /*
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

    /*
     * 处理请求体类型不支持的异常
     */
    @ExceptionHandler(UnsupportedMediaTypeStatusException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public String unsupportedMediaTypeExceptionHandler(UnsupportedMediaTypeStatusException e) {
        log.error("{} <-- {}", e.getSupportedMediaTypes(), e.getContentType());
        return e.getMessage();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String noResourceFoundExceptionHandler(NoResourceFoundException e) {
        log.warn("NOT FOUND -- {}", e.getMessage());
        return e.getMessage();
    }

    ///////////////////////////////////////////////////////////////////////////

    /*
     * 处理单个参数校验失败抛出的异常
     */
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

    /*
     * 处理请求体调用接口校验失败抛出的异常
     */
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


//    @ExceptionHandler(value = {BindException.class, ValidationException.class, MethodArgumentNotValidException.class})
//    //处理 form data方式调用接口校验失败抛出的异常
//    @ExceptionHandler(org.springframework.validation.BindException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public HashedMap<String, String> bindExceptionHandler(org.springframework.validation.BindException e) {
//        return e.getBindingResult().getFieldErrors().parallelStream()
//                .peek(v -> log.warn("{{}.{}} {} <-- [{}]", v.getObjectName(), v.getField(),
//                        v.getDefaultMessage(), v.getRejectedValue()))
//                .collect(HashedMap::new, (m, v) ->
//                                m.put(v.getField(), v.getDefaultMessage()),
//                        HashedMap::putAll);
//    }

    ///////////////////////////////////////////////////////////////////////////

    /*
     * 自定义系统异常
     */
    @ExceptionHandler(SysException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String exceptionHandler(SysException e) {
        log.error("{} <-- {}",
                e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage(),
                Arrays.stream(e.getStackTrace()).findFirst().orElse(e.getStackTrace()[0]));
        return e.getMessage() == null ? e.toString() : e.getMessage();
    }


    /*
     * 自定义业务异常
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResult<String> exceptionHandler(BizException e) {
        log.error("{} <-- {}",
                e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage(),
                Arrays.stream(e.getStackTrace()).findFirst().orElse(e.getStackTrace()[0]));
        return CommonResult.error(e.code == 200 ? 500 : e.code,
                e.getMessage() == null ? e.toString() : e.getMessage());
    }
}
