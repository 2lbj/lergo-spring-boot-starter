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
     * 鉴权过期时间
     */
    private Long authExpireSeconds;

    /**
     * 鉴权请求头名称
     */
    private String authHeaderName;

    /**
     * 是否启用Redis认证
     */
    @Deprecated
    private boolean authRedis;

    /**
     * 是否启用JWT认证
     */
    private boolean authJWT;

    /**
     * JWT鉴权过期容忍时间
     */
    private Long jwtLeewaySeconds;

    /**
     * JWT签名键值
     */
    private String jwtKey;

    /**
     * JWT签名秘钥
     */
    private String jwtSecret;

    /**
     * JWT是否刷新token
     */
    private boolean jwtRefresh;

}
