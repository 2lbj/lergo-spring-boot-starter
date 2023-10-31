package com.lergo.framework.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "lergo.env")
@Schema(title = "自定义配置")
public class LergoConfig {

    @Schema(title = "频道")
    List<ChannelTemplate> channel;

    @Data
    @Schema(title = "键值对")
    public static class ChannelTemplate {
        @Schema(title = "代码")
        Integer code;
        @Schema(title = "名称")
        String name;
    }

}
