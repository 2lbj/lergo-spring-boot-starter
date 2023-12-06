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
     * 是否启用Redis认证
     */
    @Deprecated
    private boolean authRedis;

    /**
     * 是否启用JWT认证
     */
    private boolean authJwt;

    /**
     * 鉴权过期时间
     */
    private Long authExpireSeconds = 3600L;

    /**
     * 鉴权请求头名称
     */
    private String authHeaderName = "Authorization";

}
