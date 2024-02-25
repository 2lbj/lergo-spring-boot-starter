package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "lergo")
public class LergoConfig {

    /**
     * LerGo版本
     */
    String version;
}
