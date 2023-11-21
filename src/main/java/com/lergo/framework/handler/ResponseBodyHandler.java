package com.lergo.framework.handler;

import com.lergo.framework.entity.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class ResponseBodyHandler extends ResponseBodyResultHandler {
    private static final CommonResult<?> COMMON_RESULT_SUCCESS = CommonResult.success(null);

    public ResponseBodyHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
        super(writers, resolver);
    }

    private static Mono<CommonResult<?>> methodForParams() {
        return null;
    }

    private static CommonResult<?> wrapCommonResult(Object body) {
        // 如果已经是 CommonResult 类型，则直接返回
        if (body instanceof CommonResult) {
            return (CommonResult<?>) body;
        }
        // 如果不是，则包装成 CommonResult 类型
        return CommonResult.success(body);
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Mono<Void> handleResult(@NotNull ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();
        Object body;

        // <1.1>  处理返回结果为 Mono 的情况
        if (returnValue instanceof Mono) {
            body = ((Mono<Object>) result.getReturnValue())
                    .map((Function<Object, Object>) ResponseBodyHandler::wrapCommonResult)
                    .defaultIfEmpty(COMMON_RESULT_SUCCESS);
            //  <1.2> 处理返回结果为 Flux 的情况
        } else if (returnValue instanceof Flux) {
            body = ((Flux<Object>) result.getReturnValue())
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

}