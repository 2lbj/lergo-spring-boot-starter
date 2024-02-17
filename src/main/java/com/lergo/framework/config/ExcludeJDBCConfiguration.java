package com.lergo.framework.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "lergo.boot.with-jdbc", havingValue = "false", matchIfMissing = true)
@EnableAutoConfiguration(exclude = {
        //DruidDataSourceAutoConfigure.class,
        DataSourceAutoConfiguration.class
})
public class ExcludeJDBCConfiguration {
}
