package com.lergo.framework.demo;


import com.lergo.framework.config.LergoConfig;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;


@RestController
@RequestMapping("toast")
public class ToastController {

    @Resource
    LergoConfig lergoConfig;

    @GetMapping("test")
    public Mono<String> test() {
        return Mono.just("hello world");
    }

    @GetMapping("config")
    public LergoConfig config() {
        return lergoConfig;
    }

}

