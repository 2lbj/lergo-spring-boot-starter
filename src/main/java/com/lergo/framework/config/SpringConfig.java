package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.application")
public class SpringConfig {

    /**
     * 项目名称
     */
    private String name;
    /**
     * 项目表述
     */
    private String description;
    /**
     * 唯一标识
     */
    private String artifactId;
    /**
     * 版本
     */
    private String version;

}
