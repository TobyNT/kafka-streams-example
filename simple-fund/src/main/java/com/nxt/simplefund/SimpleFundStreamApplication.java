package com.nxt.simplefund;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.nxt.simplefund.config.KafkaConfig;

@EnableConfigurationProperties(KafkaConfig.class)
@SpringBootApplication
public class SimpleFundStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleFundStreamApplication.class, args);
	}

}
