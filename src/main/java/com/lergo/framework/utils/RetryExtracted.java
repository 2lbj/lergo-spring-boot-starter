package com.lergo.framework.utils;

import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RetryExtracted {

    public static boolean booleanCallable(Callable<Boolean> callable) {
        return booleanCallable(3, 70, 3, callable);
    }

    public static boolean booleanCallable(long initialSleepTime, long increment, int attemptNumber, Callable<Boolean> callable) {
        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfResult(aBoolean -> Objects.equals(aBoolean, false))
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                //首次重试延迟3秒，最大重试70秒，每次重试间隔递增
                .withWaitStrategy(WaitStrategies.incrementingWait
                        (initialSleepTime, TimeUnit.SECONDS, increment, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()) {
                            log.error("执行次数:{} 异常: {}",
                                    attempt.getAttemptNumber(),
                                    attempt.getExceptionCause().getMessage());
                        } else {
                            if (attempt.hasResult() && attempt.getResult().equals(true)) {
                                log.info("执行次数:{} 成功", attempt.getAttemptNumber());
                                return;
                            }
                            log.warn("执行次数:{}", attempt.getAttemptNumber());
                        }
                    }
                })
                .build();
        try {
            return retryer.call(callable);
        } catch (RetryException r) {
            log.error("放弃重试 共失败 {} 次", r.getNumberOfFailedAttempts());
        } catch (ExecutionException e) {
            log.error("重试执行异常", e);
        }

        return false;
    }
}
