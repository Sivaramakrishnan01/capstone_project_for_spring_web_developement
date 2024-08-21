package com.spring.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 400)
public class IphoneStoreApplication {
	public static void main(String[] args) {
		SpringApplication.run(IphoneStoreApplication.class, args);
	}

}
