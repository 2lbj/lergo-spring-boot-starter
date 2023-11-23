package com.lergo.framework.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(value = "lergo.filter.result", havingValue = "true")
@Order(100)//优先级，数字越小，优先级越高
public class ResultFilter extends BaseFilter implements WebFilter {

    @NotNull
    public Mono<Void> filter(ServerWebExchange exchange, @NotNull WebFilterChain chain) {

        if (writeList(exchange.getRequest())) {
            return chain.filter(exchange);
        }

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

//                        switch (Objects.requireNonNull(getStatusCode())) {
//                            case OK:
//                                result.setCode(OK.value()).setMessage(OK.getReasonPhrase());
//                                break;
//                            case UNAUTHORIZED:
//                                result.setCode(UNAUTHORIZED.value()).setMessage(UNAUTHORIZED.getReasonPhrase());
//                                break;
//                            default:
//                                result.setCode(INTERNAL_SERVER_ERROR.value()).setMessage(INTERNAL_SERVER_ERROR.getReasonPhrase());
//                        }
                        result.setCode(Objects.requireNonNull(getStatusCode()).value()).setMessage(getStatusCode().getReasonPhrase());

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
    }

}