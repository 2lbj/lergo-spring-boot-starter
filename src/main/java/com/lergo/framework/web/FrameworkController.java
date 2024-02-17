package com.lergo.framework.web;


import com.lergo.framework.annotation.LogTracker;
import com.lergo.framework.annotation.UnAuthentication;
import com.lergo.framework.config.LergoConfig;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.time.Duration;


@RestController
@RequestMapping("framework")
@Tag(name = "框架", description = "框架基础接口")
public class FrameworkController {

    @Resource
    LergoConfig lergoConfig;

    @GetMapping("ping")
    @LogTracker("Ping...")
    @Operation(summary = "服务连通性测试接口", description = "cbor返回PONG")
    @UnAuthentication
    public String ping(ServerWebExchange exchange) {

        exchange.getResponse().getHeaders().add(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_CBOR_VALUE);

        return "PONG";
    }

    @GetMapping("config")
    public ResponseEntity<LergoConfig> config() {

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(lergoConfig);
    }

    @GetMapping("events")
    @Operation(summary = "SSE长连接测试接口", description = "每秒推送测试数据 中断后5秒重连",
            externalDocs = @ExternalDocumentation(url = "/html/sse.html", description = "SSE Demo"))
    public Flux<ServerSentEvent<String>> getEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> ServerSentEvent.<String>builder()
                        .id(String.valueOf(sequence))
                        .event("sse-event")
                        .data("ID - " + sequence)
                        .retry(Duration.ofSeconds(5))
                        .build());
    }

}

