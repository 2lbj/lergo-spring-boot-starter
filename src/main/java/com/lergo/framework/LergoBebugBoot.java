package com.lergo.framework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

//@SpringBootApplication
//@MapperScan("com.lergo.framework.mapper")
public class LergoBebugBoot extends LergoBootStarter {

	public static void main(String[] args) {
		SpringApplication.run(LergoBebugBoot.class, args);
	}

}
