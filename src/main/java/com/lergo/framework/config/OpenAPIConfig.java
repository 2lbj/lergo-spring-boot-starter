package com.lergo.framework.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info =
@Info(title = "基础开发脚手架",
        version = "1.0.0",
        termsOfService = "http://www.lergo.com",
        description = "OpenAPI文档",
        contact = @Contact(name = "李某人", email = "admin@lergo.com", url = "www.lergo.com"),
        license = @License(name = "GPLv3", url = "http://www.gnu.org/licenses/gpl-3.0.html")
), tags = {}
)
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi rootApi() {
        return GroupedOpenApi.builder()
                .group("root")
                .pathsToMatch("/**")
                .build();
    }

}
