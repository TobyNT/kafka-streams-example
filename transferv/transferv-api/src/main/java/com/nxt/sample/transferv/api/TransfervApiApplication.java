package com.nxt.sample.transferv.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.nxt.sample.transferv.api.configuration.KafkaConfig;

@EnableConfigurationProperties(KafkaConfig.class)
@SpringBootApplication
public class TransfervApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransfervApiApplication.class, args);
	}

}
