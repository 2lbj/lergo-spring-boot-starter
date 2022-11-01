package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "lergo.swagger")
public class SwaggerProperties {
    private Boolean enable = false;
    private String groupName = "根分组";
    private String basePackage = "com.lergo.framework";
    private String version = "1.0.0";
    private String title = "基础开发脚手架";
    private String description = "API文档";
    private String contactName = "李某人";
    private String contactEmail = "admin@lergo.com";
    private String contactUrl = "www.lergo.com";
}
