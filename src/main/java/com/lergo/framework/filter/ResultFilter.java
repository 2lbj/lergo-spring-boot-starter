package com.lergo.framework.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lergo.framework.annotation.RawResponse;
import com.lergo.framework.entity.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(value = "lergo.filter.result", havingValue = "true")
@Order(100)
public class ResultFilter extends BaseFilter implements WebFilter {

    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @NotNull
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {

        if (writeList(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        // 获取请求对应的HandlerMethod
        Mono<HandlerMethod> handlerMethodMono = requestMappingHandlerMapping
                .getHandler(exchange).cast(HandlerMethod.class);

        return handlerMethodMono.flatMap(handlerMethod -> {
            // 判断Method是否含有对应注解
            if (handlerMethod.hasMethodAnnotation(RawResponse.class)) {
                return chain.filter(exchange);
            }


            // 返回统一JSON格式
            return chain.filter(exchange.mutate().response(new ServerHttpResponseDecorator(exchange.getResponse()) {
                @NotNull
                @Override
                public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
                    return super.writeWith(DataBufferUtils.join(body).handle((buffer, sink) -> {

                        try {

                            CommonResult<Object> result = new CommonResult<>();
                            try {
                                result.setData(objectMapper.readValue(buffer.toString(StandardCharsets.UTF_8), Object.class));
                            } catch (JsonProcessingException e) {
                                log.trace(e.getMessage());
                                result.setData(buffer.toString(StandardCharsets.UTF_8));
                            }

                            result.setCode(Objects.requireNonNull(getStatusCode()).value())
                                    .setMessage(getStatusCode().getReasonPhrase());

                            setStatusCode(OK);
                            getHeaders().setContentType(MediaType.APPLICATION_JSON);
                            getHeaders().setContentLength(result.toString().getBytes(StandardCharsets.UTF_8).length);
                            sink.next(this.bufferFactory().wrap(result.toString().getBytes(StandardCharsets.UTF_8)));

                        } finally {
                            DataBufferUtils.release(buffer);
                        }

                    }));
                }
            }).build());
        });

    }

}