package com.nxt.sample.transferv.api.controller;

import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxt.sample.transferv.Action;
import com.nxt.sample.transferv.BalanceOutcome;
import com.nxt.sample.transferv.BalanceQuery;
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

	private ReplyingKafkaTemplate<String, BalanceQuery, BalanceOutcome> replyKafkaTemplate;

	private KafkaTemplate<String, FundsCommand> fundsKafkaTemplate;

	public FundController(@Autowired Clock clock, @Autowired KafkaConfig kafkaConfig,
			@Autowired @Qualifier("replyKafkaTemplate") ReplyingKafkaTemplate<String, BalanceQuery, BalanceOutcome> replyKafkaTemplate,
			@Autowired @Qualifier("fundsKafkaTemplate") KafkaTemplate<String, FundsCommand> fundsKafkaTemplate) {
		this.clock = clock;
		this.kafkaConfig = kafkaConfig;
		this.replyKafkaTemplate = replyKafkaTemplate;
		this.fundsKafkaTemplate = fundsKafkaTemplate;
	}

	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void addFunds(@RequestBody AddFundsPayload payload) throws InterruptedException, ExecutionException {
		Action actionAdd = Action.ADD;

		FundsCommand command = new FundsCommand(UUID.randomUUID().toString(), clock.instant(), payload.getCustomerId(),
				actionAdd, payload.getAmount());

		// Create a producer record
		ProducerRecord<String, FundsCommand> producerRecord = new ProducerRecord<String, FundsCommand>(
				kafkaConfig.getTopics().getFundscommand(), payload.getCustomerId(), command);

		fundsKafkaTemplate.send(producerRecord);
	}

	@GetMapping
	public BalancePayload getBalance(@RequestParam("customerId") String customerId)
			throws InterruptedException, ExecutionException {
		logger.info("Get balance of customer #{}", customerId);

		BalanceQuery query = new BalanceQuery(clock.instant(), customerId);

		// Create a producer record
		ProducerRecord<String, BalanceQuery> producerRecord = new ProducerRecord<String, BalanceQuery>(
				kafkaConfig.getTopics().getBalancequery(), customerId, query);

		// Set reply topic in header
//		producerRecord.headers().add(
//				new RecordHeader(KafkaHeaders.REPLY_TOPIC, kafkaConfig.getTopics().getBalanceoutcome().getBytes()));

		RequestReplyFuture<String, BalanceQuery, BalanceOutcome> sendAndReceive = replyKafkaTemplate
				.sendAndReceive(producerRecord);

		// Confirm whether producer complete successfully
		SendResult<String, BalanceQuery> sendResult = sendAndReceive.getSendFuture().get();
		sendResult.getProducerRecord().headers()
				.forEach(header -> logger.info(header.key() + ":" + header.value().toString()));
		logger.info("Sent {} to topic: {}\n\trecord: {}", query.getSchema().getName(),
				kafkaConfig.getTopics().getBalancequery(), query);

		// Get consumer record
		ConsumerRecord<String, BalanceOutcome> consumerRecord = sendAndReceive.get();
		BalanceOutcome outcome = consumerRecord.value();

		return new BalancePayload(outcome.getAmount());
	}
}
