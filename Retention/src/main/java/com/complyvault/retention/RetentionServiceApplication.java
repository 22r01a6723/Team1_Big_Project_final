package com.complyvault.retention;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableScheduling
public class RetentionServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(RetentionServiceApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(RetentionServiceApplication.class, args);
        } catch (Exception e) {
            logger.error("Application failed to start", e);
            // Optionally, exit with error code
            System.exit(1);
        }
    }
}
