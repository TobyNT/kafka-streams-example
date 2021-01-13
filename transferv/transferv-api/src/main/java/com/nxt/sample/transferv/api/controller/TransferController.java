package com.nxt.sample.transferv.api.controller;

import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nxt.sample.transferv.Action;
import com.nxt.sample.transferv.FundsCommand;
import com.nxt.sample.transferv.api.configuration.KafkaConfig;
import com.nxt.sample.transferv.api.controller.payload.TransferFundsPayload;

@RestController
@RequestMapping("/transfer")
public class TransferController {
	private static final Logger logger = LoggerFactory.getLogger(TransferController.class);

	private Clock clock;

	private KafkaConfig kafkaConfig;

	private KafkaProducer<String, SpecificRecord> kafkaProducer;

	@Autowired
	public TransferController(Clock clock, KafkaConfig kafkaConfig, KafkaProducer<String, SpecificRecord> kafkaProducer) {
		this.clock = clock;
		this.kafkaConfig = kafkaConfig;
		this.kafkaProducer = kafkaProducer;
	}

	@PostMapping
	public void addFunds(@RequestBody TransferFundsPayload payload) throws InterruptedException, ExecutionException {
		// TODO: not implemented yet
	}
}
