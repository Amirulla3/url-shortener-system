package com.shorter_url.shorter_service;

import com.shorter_url.shorter_service.configuration.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableCaching
public class ShorterServiceApplication {

	public static void main(String[] args) {

        SpringApplication.run(ShorterServiceApplication.class, args);

	}

}
