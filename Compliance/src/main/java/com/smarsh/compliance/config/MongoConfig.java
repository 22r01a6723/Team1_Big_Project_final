package com.smarsh.compliance.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.smarsh.compliance.mongodb")
public class MongoConfig {
    // Spring Boot auto-configuration will handle MongoDB setup
}
