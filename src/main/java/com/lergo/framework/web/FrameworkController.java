package com.lergo.framework.web;


import com.lergo.framework.annotation.LogTracker;
import com.lergo.framework.config.LergoConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.validation.Valid;


@RestController
@RequestMapping("framework")
@Tag(name = "框架", description = "框架基础接口")
public class FrameworkController {

    @Resource
    LergoConfig lergoConfig;

    @GetMapping("ping")
    @LogTracker("~Ping-Pong LogTracker~")
    @Operation(summary = "服务连通性测试接口")
    public String ping(@Valid
                       @Nullable
                       @Parameter(description = "参数")
                       String request) {
        return "PONG:" + request;
    }

    @GetMapping("config")
    public LergoConfig config() {
        return lergoConfig;
    }

}

