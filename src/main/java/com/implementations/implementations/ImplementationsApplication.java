package com.implementations.implementations;

import com.implementations.implementations.configurations.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ImplementationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImplementationsApplication.class, args);
	}

}
