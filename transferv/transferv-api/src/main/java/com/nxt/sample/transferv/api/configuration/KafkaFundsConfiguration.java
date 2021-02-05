package com.nxt.sample.transferv.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.nxt.sample.transferv.FundsCommand;

@Configuration
public class KafkaFundsConfiguration {

	@Bean("fundsKafkaTemplate")
	public KafkaTemplate<String, FundsCommand> fundsKafkaTemplate(
			ProducerFactory producerFactory) {
		return new KafkaTemplate<String, FundsCommand>(producerFactory);
	}

}
