package com.lergo.framework.filter;

import com.google.gson.Gson;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class BaseFilter {

    Gson gson = new Gson();
    boolean writeList(ServerHttpRequest req) {
        return req.getPath().value().equals("/") ||
                req.getPath().value().startsWith("/actuator") ||
                req.getPath().value().startsWith("/v3/api-docs") ||
                req.getPath().value().equals("/swagger-ui.html") ||
                req.getPath().value().startsWith("/webjars");
    }
}
