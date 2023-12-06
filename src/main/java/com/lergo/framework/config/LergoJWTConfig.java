package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lergo.jwt")
public class LergoJWTConfig {

    /**
     * JWT鉴权过期容忍时间
     */
    private Long leewaySeconds = 120L;

    /**
     * JWT签名键值
     */
    private String appKey = "lerGo";

    /**
     * JWT签名秘钥
     */
    private String appSecret = "io.github.2lbj";

    /**
     * JWT是否刷新token
     */
    private boolean refresh;

}
