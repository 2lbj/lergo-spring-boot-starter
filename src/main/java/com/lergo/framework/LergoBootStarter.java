package com.lergo.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LergoBootStarter extends AbstractBootStarter {
	private static final Logger log = LoggerFactory.getLogger(LergoBootStarter.class);

	public LergoBootStarter() {
		log.info(System.getProperties().getProperty("os.name"));
	}

}
