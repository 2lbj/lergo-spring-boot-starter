package com.lergo.framework.filter;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;

public class BodyCaptureExchange extends ServerWebExchangeDecorator {

    BodyCaptureRequest bodyCaptureRequest;

    BodyCaptureResponse bodyCaptureResponse;

    public BodyCaptureExchange(ServerWebExchange exchange) {
        super(exchange);
        this.bodyCaptureRequest = new BodyCaptureRequest(exchange.getRequest());
        this.bodyCaptureResponse = new BodyCaptureResponse(exchange.getResponse());
    }

    @NotNull
    @Override
    public BodyCaptureRequest getRequest() {
        return bodyCaptureRequest;
    }

    @NotNull
    @Override
    public BodyCaptureResponse getResponse() {
        return bodyCaptureResponse;
    }

    public String getFormDataString() {
        StringBuilder form = new StringBuilder();
        super.getFormData().subscribe(map ->
                form.append(map.keySet())
        );
        return form.toString();
    }
}