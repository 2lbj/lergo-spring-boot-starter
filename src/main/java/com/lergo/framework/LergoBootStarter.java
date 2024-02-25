package com.lergo.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class LergoBootStarter extends AbstractBootStarter {
    private static final Logger log = LoggerFactory.getLogger(LergoBootStarter.class);

    public LergoBootStarter() {
        log.info("{}-{} ({}) - Java: {}",
                System.getProperties().getProperty("os.name"),
                System.getProperties().getProperty("os.version"),
                System.getProperties().getProperty("os.arch"),
                System.getProperties().getProperty("java.version")
        );
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(LergoBootStarter.class, args);
        System.out.println("Spring BOOT Running: $" + Arrays.toString(args));
        System.out.printf("\n\n\t\tREST-API: http://localhost:%s%n\n\n",
                context.getEnvironment().getProperty("server.port"));
    }

}
