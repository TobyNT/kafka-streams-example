package com.nxt.sample.transferv.funds.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.streams.KafkaStreams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaStreamService {

	@Autowired
	private KafkaStreams streams;

	@PostConstruct
	public void init() {
		streams.start();
	}

	@PreDestroy
	public void destroy() {
		streams.close();
	}
}
