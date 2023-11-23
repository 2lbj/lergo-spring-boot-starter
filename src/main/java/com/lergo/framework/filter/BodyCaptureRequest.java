package com.lergo.framework.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

public class BodyCaptureRequest extends ServerHttpRequestDecorator {

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
        return this.body.toString().replaceAll("[\r\n ]", "");
    }

}
