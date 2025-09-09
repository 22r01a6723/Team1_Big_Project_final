package com.complyvault.retention.service.strategy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetentionPolicyStrategyFactoryConfig {
    @Bean
    public RetentionPolicyStrategyFactory retentionPolicyStrategyFactory() {
        return new RetentionPolicyStrategyFactory();
    }
}

