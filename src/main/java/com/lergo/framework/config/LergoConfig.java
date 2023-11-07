package com.lergo.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "lergo")
public class LergoConfig {

    /**
     * 死道友不死贫道
     */
    List<ChannelTemplate> channel;

    /**
     * 键值对
     */
    @Data
    public static class ChannelTemplate {

        /**
         * 代码
         */
        Integer code;

        /**
         * 名称
         */
        String name;
    }

}
