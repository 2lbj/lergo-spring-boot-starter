package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lergo.handler")
public class LergoHandlerConfig {

    /**
     * 是否返回通用JSON结果
     */
    private boolean result;

}
