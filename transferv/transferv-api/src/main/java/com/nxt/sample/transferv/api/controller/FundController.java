package com.nxt.sample.transferv.api.controller;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxt.sample.transferv.Action;
import com.nxt.sample.transferv.FundsCommand;
import com.nxt.sample.transferv.api.configuration.KafkaConfig;
import com.nxt.sample.transferv.api.controller.payload.AddFundsPayload;
import com.nxt.sample.transferv.share.payload.BalancePayload;

@RestController
@RequestMapping("/funds")
public class FundController {

	private static final Logger logger = LoggerFactory.getLogger(FundController.class);

	private Clock clock;

	private KafkaConfig kafkaConfig;

	private KafkaProducer<String, SpecificRecord> kafkaProducer;

	@Autowired
	public FundController(Clock clock, KafkaConfig kafkaConfig, KafkaProducer<String, SpecificRecord> kafkaProducer) {
		this.clock = clock;
		this.kafkaConfig = kafkaConfig;
		this.kafkaProducer = kafkaProducer;
	}

	@PostMapping
	public void addFunds(@RequestBody AddFundsPayload payload) throws InterruptedException, ExecutionException {
		FundsCommand command = new FundsCommand(UUID.randomUUID().toString(), clock.instant(), payload.getCustomerId(),
				Action.ADD, payload.getAmount());

		ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
				kafkaConfig.getTopics().getFundscommand(), payload.getCustomerId(), command);

		kafkaProducer.send(producerRecord).get();

		logger.info("Sent {} to topic: {}\n\trecord: {}", command.getSchema().getName(),
				kafkaConfig.getTopics().getFundscommand(), command);
	}

	@GetMapping
	public BalancePayload getBalance(@RequestParam("customerId") String customerId)
			throws InterruptedException, ExecutionException {
		logger.info("Get balance of customer #{}", customerId);

		FundsCommand command = new FundsCommand(UUID.randomUUID().toString(), clock.instant(), customerId, Action.GET,
				BigDecimal.ZERO);

		ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
				kafkaConfig.getTopics().getFundscommand(), customerId, command);

		kafkaProducer.send(producerRecord).get();

		return new BalancePayload(BigDecimal.ZERO);
	}
}
