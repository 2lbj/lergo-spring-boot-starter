package com.lergo.framework.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

public class APIFeignConfig {

    @Bean
    public Request.Options feignOptions() {
        return new Request.Options(
                3L,
                TimeUnit.SECONDS,
                10L,
                TimeUnit.SECONDS,
                true);
    }

    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(
                100,
                TimeUnit.SECONDS.toMillis(1),
                2);
    }

}
