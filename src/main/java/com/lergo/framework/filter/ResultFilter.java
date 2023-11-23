package com.lergo.framework.filter;

import com.lergo.framework.entity.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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

                return super.writeWith(DataBufferUtils.join(body).map(buffer -> {
                    try {

                        //优化以下代码
                        String r = buffer.toString(StandardCharsets.UTF_8);
                        JSONObject result;
                        try {
                            result = new JSONObject(r);
                        } catch (JSONException e) {
                            result = new JSONObject();
                        }

                        switch (Objects.requireNonNull(getStatusCode())) {
                            case OK:
                                r = CommonResult.success(result).toString();
                                break;
                            case UNAUTHORIZED:
                                r = CommonResult.error(UNAUTHORIZED.value(), r).toString();
                                break;
                            default:
                                r = CommonResult.error(getStatusCode().value(), r).toString();
                        }

                        setStatusCode(OK);
                        getHeaders().setContentType(MediaType.APPLICATION_JSON);
                        getHeaders().setContentLength(r.getBytes(StandardCharsets.UTF_8).length);
                        return this.bufferFactory().wrap(r.getBytes(StandardCharsets.UTF_8));
                    } finally {
                        DataBufferUtils.release(buffer);
                    }
                }));
            }

        }).build());
    }

}