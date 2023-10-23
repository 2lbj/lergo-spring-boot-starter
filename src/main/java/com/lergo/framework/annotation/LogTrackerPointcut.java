package com.lergo.framework.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@Lazy(false)
public class LogTrackerPointcut {

    @Pointcut("@annotation(com.lergo.framework.annotation.LogTracker)")
    private void cutMethod() {
    }

    @Around("cutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        // 获取方法传入参数
        Object[] params = joinPoint.getArgs();
        // 获取方法
        Method method = getDeclaredAnnotation(joinPoint);
        // 执行源方法
        Object proceed = joinPoint.proceed();

        log.info("{} @LogTracker.{} {}| ${} --> {}",
                method.getDeclaringClass().getName(),
                method.getName(),
                method.getDeclaredAnnotation(LogTracker.class).value(),
                params,
                proceed);

        return proceed;
    }

    /**
     * 获取方法中声明的注解
     *
     * @return Method
     */
    public Method getDeclaredAnnotation(ProceedingJoinPoint joinPoint) {

        if (joinPoint instanceof MethodInvocationProceedingJoinPoint) {
            return ((MethodSignature) joinPoint.getSignature()).getMethod();
        }

        log.error("get @LogTracker Clazz error, joinPoint: {}", joinPoint);
        return null;
    }


}
