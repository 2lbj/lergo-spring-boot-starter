package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lergo.filter")
public class LergoFilterConfig {

    /**
     * 是否启用计时器
     */
    private boolean timer;

    /**
     * 是否返回通用JSON结果
     */
    private boolean result;

    /**
     * 是否启用鉴权
     */
    private boolean authJWT;

    /**
     * 请求头过期时间
     */
    private Long authExpireSeconds;

    /**
     * 请求头名称
     */
    private String authHeaderName;

    /**
     * 是否启用Redis认证
     */
    @Deprecated
    private boolean authRedis;
}
