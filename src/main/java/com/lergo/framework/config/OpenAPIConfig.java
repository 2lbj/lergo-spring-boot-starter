package com.lergo.framework.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.Data;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info =
@Info(title = "${lergo.open-api.title:基础开发脚手架}",
        version = "${lergo.open-api.version:v1.0.0}",
        termsOfService = "http://www.lergo.com",
        description = "OpenAPI文档",
        contact = @Contact(name = "李某人", email = "admin@lergo.com", url = "www.lergo.com"),
        license = @License(name = "GPLv3", url = "http://www.gnu.org/licenses/gpl-3.0.html")
), tags = {})
@ConfigurationProperties(prefix = "lergo.open-api")
@Data
public class OpenAPIConfig {

    /**
     * 标题 eg: 基础开发脚手架
     */
    private String title;

    /**
     * 版本号 eg: v1.0.0
     */
    private String version;

    @Bean
    public GroupedOpenApi rootApi() {
        return GroupedOpenApi.builder()
                .group("root")
                .pathsToMatch("/**")
                .build();
    }

}
