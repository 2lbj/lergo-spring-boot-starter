package com.lergo.framework.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class BaseFilter {

    Gson gson = new Gson();
    ObjectMapper objectMapper = new ObjectMapper();
    boolean writeList(ServerHttpRequest req) {
        return req.getPath().value().equals("/") ||
                req.getPath().value().startsWith("/actuator") ||
                req.getPath().value().equals("/v3/api-docs") ||
                req.getPath().value().equals("/swagger-ui.html") ||
                req.getPath().value().startsWith("/webjars") ||
                req.getPath().value().contains(".html") ||
                req.getPath().value().startsWith("/html");
    }
}
