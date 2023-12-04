package com.lergo.framework.filter;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(1)//优先级，数字越小，优先级越高
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

            if (!HttpStatus.OK.equals(bce.getResponse().getStatusCode())) {
                log.warn("[{}] {} Headers:{} Form:{} Body:{} !----> ({}) `{}`",
                        bce.getRequest().getMethod(),
                        bce.getRequest().getURI(),
                        bce.getRequest().getHeaders(),
                        bce.getFormDataString(),
                        bce.getRequest().getBodyStr(),
                        bce.getResponse().getStatusCode(),
                        bce.getResponse().getBodyStr());
                return;
            }

            // REST日志
            log.trace("[{}] {} Headers:{} Form:{} Body:{} |====> ({}) `{}`",
                    bce.getRequest().getMethod(),
                    bce.getRequest().getURI(),
                    bce.getRequest().getHeaders(),
                    bce.getFormDataString(),
                    bce.getRequest().getBodyStr(),
                    bce.getResponse().getStatusCode(),
                    bce.getResponse().getBodyStr());
        });
    }

    static class BodyCaptureExchange extends ServerWebExchangeDecorator {
        BodyCaptureRequest request;
        BodyCaptureResponse response;

        public BodyCaptureExchange(ServerWebExchange exchange) {
            super(exchange);
            this.request = new BodyCaptureRequest(exchange.getRequest());
            this.response = new BodyCaptureResponse(exchange.getResponse());
        }

        @NotNull
        @Override
        public BodyCaptureRequest getRequest() {
            return request;
        }

        @NotNull
        @Override
        public BodyCaptureResponse getResponse() {
            return response;
        }

        public String getFormDataString() {
            StringBuilder form = new StringBuilder();
            super.getFormData().subscribe(map ->
                    form.append(map.keySet())
            );
            return form.toString();
        }

        static class BodyCaptureRequest extends ServerHttpRequestDecorator {
            private final StringBuilder body = new StringBuilder();

            public BodyCaptureRequest(ServerHttpRequest delegate) {
                super(delegate);
            }

            @NotNull
            public Flux<DataBuffer> getBody() {
                return super.getBody().doOnNext((DataBuffer buffer) -> {
                    body.append(StandardCharsets.UTF_8.decode(buffer.asByteBuffer()));
                });
            }

            public String getBodyStr() {
                return this.body.toString().replaceAll("[\r\n]", "  ");
            }
        }

        static class BodyCaptureResponse extends ServerHttpResponseDecorator {
            private final StringBuilder body = new StringBuilder();

            public BodyCaptureResponse(ServerHttpResponse delegate) {
                super(delegate);
            }

            @NotNull
            @Override
            public Mono<Void> writeWith(@NotNull Publisher<? extends DataBuffer> body) {
                return super.writeWith(Flux.from(body).doOnNext((DataBuffer buffer) -> {
                    this.body.append(StandardCharsets.UTF_8.decode(buffer.asByteBuffer()));
                }));
            }

            public String getBodyStr() {
                return this.body.toString().replaceAll("[\r\n]", "  ");
            }
        }

    }
}