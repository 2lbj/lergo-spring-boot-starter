package com.lergo.framework.filter;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@Slf4j
@Configuration
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnProperty(value = "lergo.filter.timer", havingValue = "true")
@Order(-1)
public class TimerFilter extends BaseFilter implements WebFilter {

    private final Timer filterTimer;

    @Autowired
    public TimerFilter(MeterRegistry registry) {
        /*
            http://localhost:9999/actuator/metrics/lergo.filter.timer
         */
        filterTimer = Timer.builder("lergo.filter.timer")
                .description("LerGo Filter Execution Time")
                .register(registry);
    }

    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange,
                             @NotNull WebFilterChain chain) {

        if (writeList(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        long start = System.currentTimeMillis();
        return chain.filter(exchange)
                .doFinally(signalType -> {
                    filterTimer.record((System.currentTimeMillis() - start),
                            java.util.concurrent.TimeUnit.MILLISECONDS);
                });
    }
}
