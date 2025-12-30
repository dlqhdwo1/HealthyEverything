package com.example.healthyeverythingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class HealthyEverythingApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(HealthyEverythingApiApplication.class, args);
        System.out.println("test");
        System.out.println("test2");
	}
}
