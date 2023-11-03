package com.lergo.framework.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Slf4j
@Configuration
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class LoggerFilter implements WebFilter {
//    @Resource
//    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final Timer myFilterTimer;

    @Autowired
    public LoggerFilter(MeterRegistry registry) {
        myFilterTimer = Timer.builder("my.filter.timer")
                .description("My Filter Execution Time")
                .register(registry);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long start = System.currentTimeMillis();

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long end = System.currentTimeMillis();
                    myFilterTimer.record((end - start), java.util.concurrent.TimeUnit.MILLISECONDS);

                });
    }

//    @NotNull
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//        ServerHttpRequest req = exchange.getRequest();
//        ServerHttpResponse res = exchange.getResponse();
//
//        Flux<DataBuffer> body = req.getBody();
//
//        AtomicReference<String> bodyRef = new AtomicReference<>();
//        body.subscribe(buffer -> {
//            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
//            DataBufferUtils.release(buffer);
//            bodyRef.set(charBuffer.toString());
//        });
//
//        log.error("{} {} <<{}>> --> ({}) {}",req.getURI(),req.getHeaders(),bodyRef.get(),res.getStatusCode(),res.getHeaders());
//
//        //return chain.filter(exchange);
//        // 获取请求对应的HandlerMethod
//        Mono<HandlerMethod> handlerMethodMono = requestMappingHandlerMapping
//                .getHandler(exchange).cast(HandlerMethod.class);
//
//        return handlerMethodMono.zipWhen(handlerMethod -> {
//            return chain.filter(exchange);
//        }).map(Tuple2::getT2);
//    }
}
