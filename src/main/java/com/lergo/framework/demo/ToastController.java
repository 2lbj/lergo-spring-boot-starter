package com.lergo.framework.demo;


import com.lergo.framework.annotation.LogTracker;
import com.lergo.framework.config.LergoConfig;
import com.lergo.framework.mapper.NoWarnMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@RequestMapping("toast")
public class ToastController {

    @Resource
    LergoConfig lergoConfig;
    @Resource
    NoWarnMapper noWarnMapper;

    @GetMapping("test")
    @LogTracker("Hello World~")
    public String test() {
        return "hello world";
    }

    @GetMapping("config")
    public LergoConfig config() {
        return lergoConfig;
    }

    @GetMapping("dbVersion")
    public String dbVersion() {
        return noWarnMapper.version();
    }

}

