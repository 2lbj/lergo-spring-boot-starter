package com.lergo.framework.config;

import com.lergo.framework.handler.ResponseBodyHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;

@Configuration
@ConditionalOnProperty(value = "lergo.result.enable", havingValue = "true")
public class ResponseConfiguration {
    @Bean
    public ResponseBodyHandler responseWrapper(ServerCodecConfigurer serverCodecConfigurer,
                                               RequestedContentTypeResolver requestedContentTypeResolver) {
        return new ResponseBodyHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }

}