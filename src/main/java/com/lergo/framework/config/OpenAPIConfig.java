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
@Info(title = "${open-api.title:${spring.application.name:基础开发脚手架}}",
        version = "${open-api.version:${application.version:v1.0.0}}",
        termsOfService = "${open-api.version:https://github.com/2lbj/lergo-spring-boot-starter}",
        description = "${open-api.description:OpenAPI文档}",
        contact = @Contact(name = "李某人", email = "nerv.2lbj@gmail.com", url = "https://2lbj.github.io"),
        license = @License(name = "GPLv3", url = "http://www.gnu.org/licenses/gpl-3.0.html")
), tags = {})
@ConfigurationProperties(prefix = "open-api")
@Data
public class OpenAPIConfig {

    /**
     * 标题 eg: 李二狗的脚手架
     */
    private String title;

    /**
     * 版本号 eg: v1.0.0
     */
    private String version;

    /**
     * 服务条款 eg:
     */
    private String termsOfService;

    /**
     * 描述 eg: 少既是多, 能不说就别说
     */
    private String description;

    @Bean
    public GroupedOpenApi rootApi() {
        return GroupedOpenApi.builder()
                .group("root")
                .pathsToMatch("/**")
                .build();
    }

}
