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
     * 是否启用鉴权
     */
    private boolean auth;
    /**
     * 请求头校验名称
     */
    private String authHeaderName;

    /**
     * 是否返回通用JSON结果
     */
    private boolean result;

}
