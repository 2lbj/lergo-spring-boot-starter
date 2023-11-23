package com.lergo.framework.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@Order(1)
public class LogFilter extends BaseFilter implements WebFilter {
    @NotNull
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange,
                             @NotNull WebFilterChain chain) {

        if (writeList(exchange.getRequest())) {
            return chain.filter(exchange);
        }

        BodyCaptureExchange bce = new BodyCaptureExchange(exchange);
        return chain.filter(bce).doFinally((se) -> {
            log.trace("[{}] {} Headers:{} Form:{} Body:{} ==> ({}) {}",
                    bce.getRequest().getMethod(),
                    bce.getRequest().getURI(),
                    bce.getRequest().getHeaders(),
                    bce.getFormDataString(),
                    bce.getRequest().getFullBody(),
                    bce.getResponse().getStatusCode(),
                    bce.getResponse().getFullBody());
            if (!HttpStatus.OK.equals(bce.getResponse().getStatusCode())) {
                log.error("[{}] {} Headers:{} Form:{} Body:{} |====> ({}) {}",
                        bce.getRequest().getMethod(),
                        bce.getRequest().getURI(),
                        bce.getRequest().getHeaders(),
                        bce.getFormDataString(),
                        bce.getRequest().getFullBody(),
                        bce.getResponse().getStatusCode(),
                        bce.getResponse().getFullBody());
            }
        });
    }
}