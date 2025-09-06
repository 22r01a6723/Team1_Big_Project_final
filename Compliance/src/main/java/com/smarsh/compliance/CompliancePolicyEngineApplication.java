package com.smarsh.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class CompliancePolicyEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompliancePolicyEngineApplication.class, args);
	}

}
