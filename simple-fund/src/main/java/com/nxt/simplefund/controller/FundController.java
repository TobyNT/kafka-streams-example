package com.nxt.simplefund.controller;

import java.time.Clock;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nxt.simplefund.BalanceState;
import com.nxt.simplefund.FundsAdded;
import com.nxt.simplefund.config.KafkaConfig;
import com.nxt.simplefund.controller.payload.AddFundsPayload;
import com.nxt.simplefund.controller.payload.BalancePayload;
import com.nxt.simplefund.utility.NetUtils;

import feign.Feign;
import feign.jackson.JacksonDecoder;

@RestController
@RequestMapping("/funds")
public class FundController {

	private Logger logger = LoggerFactory.getLogger(FundController.class);

	@Autowired
	private Clock clock;

	@Autowired
	private KafkaConfig kafkaConfig;

	@Autowired
	private KafkaProducer<String, SpecificRecord> kafkaProducer;

	@Autowired
	private HostInfo currentHostInfo;

	@Autowired
	private KafkaStreams kafkaStreams;

	@PostMapping
	public void addFunds(@RequestBody AddFundsPayload payload) throws InterruptedException, ExecutionException {
		FundsAdded record = new FundsAdded(UUID.randomUUID().toString(), clock.instant(), payload.getCustomerId(),
				payload.getAmount());

		ProducerRecord<String, SpecificRecord> producerRecord = new ProducerRecord<String, SpecificRecord>(
				kafkaConfig.getTopics().getBalance(), payload.getCustomerId(), record);

		kafkaProducer.send(producerRecord).get();

		logger.info("Sent {} to topic: {}\n\trecord: {}", record.getSchema().getName(),
				kafkaConfig.getTopics().getBalance(), record);
	}

	@GetMapping
	public BalancePayload getBalance(@RequestParam("customerId") String customerId) {
		logger.info("Get balance of customer #{}", customerId);
		String storeName = kafkaConfig.getStateStores().getBalanceReadModel();
		StreamsMetadata metadata = kafkaStreams.metadataForKey(storeName, customerId, new StringSerializer());

		HostInfo hostInfo = metadata.hostInfo();

		if (currentHostInfo.equals(hostInfo)) {
			logger.info("Reading local state store on {}", NetUtils.hostInfoToUrl(hostInfo));

			ReadOnlyKeyValueStore<String, BalanceState> store = kafkaStreams.store(storeName,
					QueryableStoreTypes.<String, BalanceState>keyValueStore());

			return new BalancePayload(store.get(customerId).getAmount());
		} else {
			String baseUrl = NetUtils.hostInfoToUrl(hostInfo);
			logger.info("Proxy to remote state store on {}", baseUrl);

			FundBalanceAdaptor client = Feign.builder().decoder(new JacksonDecoder()).target(FundBalanceAdaptor.class,
					"http://" + baseUrl);

			return client.getBalance(customerId);
		}
	}
}
