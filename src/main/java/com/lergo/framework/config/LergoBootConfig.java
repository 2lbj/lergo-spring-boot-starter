package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lergo.boot")
public class LergoBootConfig {

    /**
     * 启动是否加载JDBC
     */
    private boolean withJdbc;

    /**
     * 启动是否加载Redis
     */
    private boolean withRedis;

}
