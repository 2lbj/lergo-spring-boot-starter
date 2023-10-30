package com.lergo.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LergoBootStarter extends AbstractBootStarter {
	private static final Logger log = LoggerFactory.getLogger(LergoBootStarter.class);

	public LergoBootStarter() {
		//System.getProperties().getProperty("os.name");
	}

	static {
		//System.setProperty("toast.demo", "akb48");
		//log.debug(System.getProperties().toString());
	}
}
