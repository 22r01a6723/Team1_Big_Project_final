package com.complyvault.audit.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {
    // MongoDB auto-index-creation is enabled in application.properties
    // Collections will be automatically created when first document is inserted
}
