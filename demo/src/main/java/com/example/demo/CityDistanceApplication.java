package com.example.demo;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CityDistanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CityDistanceApplication.class, args);
	}


}
