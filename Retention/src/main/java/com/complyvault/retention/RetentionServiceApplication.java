package com.complyvault.retention;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RetentionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RetentionServiceApplication.class, args);
    }
}
