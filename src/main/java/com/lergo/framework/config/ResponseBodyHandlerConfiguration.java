package com.lergo.framework.config;

import com.lergo.framework.entity.CommonResult;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Configuration
@ConditionalOnProperty(value = "lergo.handler.result", havingValue = "true")
@Deprecated
public class ResponseBodyHandlerConfiguration {
    @Bean
    ResponseBodyHandler responseWrapper(ServerCodecConfigurer serverCodecConfigurer,
                                               RequestedContentTypeResolver requestedContentTypeResolver) {
        return new ResponseBodyHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }

    @Slf4j
    private static class ResponseBodyHandler extends ResponseBodyResultHandler {
        private static final CommonResult<?> COMMON_RESULT_SUCCESS = CommonResult.success(null);

        public ResponseBodyHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
            super(writers, resolver);
        }

        private static CommonResult<?> wrapCommonResult(Object body) {
            // 如果已经是 CommonResult 类型，则直接返回
            if (body instanceof CommonResult) {
                return (CommonResult<?>) body;
            }
            // 如果不是，则包装成 CommonResult 类型
            return CommonResult.success(body);
        }

        @Override
        @NotNull
        @SuppressWarnings("unchecked")
        public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {

            Object returnValue = result.getReturnValue();
            Object body;

            if (writeList(exchange.getRequest())) {
                return super.handleResult(exchange, result);
            }

            // <1.1>  处理返回结果为 Mono 的情况
            if (returnValue instanceof Mono) {
                body = ((Mono<Object>) Objects.requireNonNull(result.getReturnValue()))
                        .map((Function<Object, Object>) ResponseBodyHandler::wrapCommonResult)
                        .defaultIfEmpty(COMMON_RESULT_SUCCESS);
                //  <1.2> 处理返回结果为 Flux 的情况
            } else if (returnValue instanceof Flux) {
                body = ((Flux<Object>) Objects.requireNonNull(result.getReturnValue()))
                        .collectList()
                        .map((Function<Object, Object>) ResponseBodyHandler::wrapCommonResult)
                        .defaultIfEmpty(COMMON_RESULT_SUCCESS);
                //  <1.3> 处理结果为其它类型
            } else {
                body = wrapCommonResult(returnValue);
            }

            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return response.writeWith(Flux.just(response.bufferFactory().wrap(
                    body.toString().getBytes(StandardCharsets.UTF_8)
            )));
        }

        boolean writeList(ServerHttpRequest req) {
            return req.getPath().value().equals("/") ||
                    req.getPath().value().startsWith("/actuator") ||
                    req.getPath().value().startsWith("/v3/api-docs") ||
                    req.getPath().value().equals("/swagger-ui.html") ||
                    req.getPath().value().startsWith("/webjars");
        }

    }

}
