package com.nxt.sample.transferv.funds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.nxt.sample.transferv.funds.configuration.KafkaConfig;

@EnableConfigurationProperties(KafkaConfig.class)
@SpringBootApplication
public class FundsStreamApplication {

	public static void main(String[] args) {
		SpringApplication.run(FundsStreamApplication.class, args);
	}

}
