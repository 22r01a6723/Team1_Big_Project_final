package com.project_1.normalizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableMongoAuditing
public class NormalizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NormalizerApplication.class, args);
	}

}
