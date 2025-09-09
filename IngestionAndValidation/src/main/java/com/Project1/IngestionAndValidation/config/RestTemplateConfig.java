package com.Project1.IngestionAndValidation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.complyvault.shared.client.AuditClient;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public AuditClient auditClient(RestTemplate restTemplate) {
        return new AuditClient(restTemplate);
    }
}
