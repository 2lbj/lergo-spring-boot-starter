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

import static org.springframework.http.HttpStatus.*;

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

                    setStatusCode(OK);
                    getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    try {

                        String r = buffer.toString(StandardCharsets.UTF_8);

                        switch (Objects.requireNonNull(getStatusCode())) {
                            case OK:
                                r = CommonResult.success(objectMapper.readValue(r, Object.class))
                                        .setMessage("SUCCESS").toString();
                                break;
                            case UNAUTHORIZED:
                                r = CommonResult.error(UNAUTHORIZED.value(), r)
                                        .setMessage("UNAUTHORIZED").toString();
                                break;
                            default:
                                r = CommonResult.error(getStatusCode().value(), r)
                                        .setMessage("UNKNOWN ERROR").toString();
                        }

                        getHeaders().setContentLength(r.getBytes(StandardCharsets.UTF_8).length);
                        sink.next(this.bufferFactory().wrap(r.getBytes(StandardCharsets.UTF_8)));

                    } catch (JsonProcessingException e) {

                        log.error("JsonProcessingException", e);
                        String r = CommonResult.error(INTERNAL_SERVER_ERROR.value(), e.getMessage()).toString();

                        getHeaders().setContentLength(r.getBytes(StandardCharsets.UTF_8).length);
                        sink.next(this.bufferFactory().wrap(r.getBytes(StandardCharsets.UTF_8)));

                    } finally {
                        DataBufferUtils.release(buffer);
                    }
                }));
            }

        }).build());
    }

}