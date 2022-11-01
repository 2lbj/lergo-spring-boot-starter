package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "lergo.env")
public class LergoConfig {

    List<ChannelTemplate> channel;

    @Data
    public static class ChannelTemplate {
        Integer code;
        String name;
    }

}
