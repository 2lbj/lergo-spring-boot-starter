package com.lergo.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class LergoBootStarter extends AbstractBootStarter {
	private static final Logger log = LoggerFactory.getLogger(LergoBootStarter.class);

	public LergoBootStarter() {
		System.setProperty("pagehelper.banner", "false");
		log.info("{}-{} ({})",
				System.getProperties().getProperty("os.name"),
				System.getProperties().getProperty("os.version"),
				System.getProperties().getProperty("os.arch")
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(LergoBootStarter.class, args);
        System.out.println("Spring BOOT Startup: $" + Arrays.toString(args));
	}

}
