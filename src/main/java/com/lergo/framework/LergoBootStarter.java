package com.lergo.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

//@EnableOpenApi
@SpringBootApplication
public class LergoBootStarter extends AbstractBootStarter {
	private static final Logger log = LoggerFactory.getLogger(LergoBootStarter.class);

	public LergoBootStarter() {
	}

	static {
		System.setProperty("toast.demo", "akb48");
		log.debug(System.getProperties().toString());
	}
}
