package com.lergo.framework.filter;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class BodyCaptureResponse extends ServerHttpResponseDecorator {

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
        return this.body.toString().replaceAll("[\r\n ]", "");
    }

}