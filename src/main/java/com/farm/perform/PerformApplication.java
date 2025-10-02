package com.farm.perform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = "com.farm.perform.global.jwt")
public class PerformApplication {

	public static void main(String[] args) {
		SpringApplication.run(PerformApplication.class, args);
	}

}
