package com.nxt.sample.transferv.funds.service;

import java.math.BigDecimal;
import java.time.Clock;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import com.nxt.sample.transferv.BalanceOutcome;
import com.nxt.sample.transferv.BalanceQuery;
import com.nxt.sample.transferv.BalanceState;
import com.nxt.sample.transferv.funds.configuration.KafkaConfig;
import com.nxt.sample.transferv.funds.utility.NetUtils;

@Component
public class BalanceQueryListener {
	private static final Logger logger = LoggerFactory.getLogger(BalanceQueryListener.class);

	private KafkaConfig kafkaConfig;

	private KafkaStreams kafkaStreams;

	private Clock clock;

	@Autowired
	public BalanceQueryListener(Clock clock, KafkaConfig kafkaConfig, KafkaStreams kafkaStreams) {
		this.clock = clock;
		this.kafkaConfig = kafkaConfig;
		this.kafkaStreams = kafkaStreams;
	}

	@KafkaListener(topics = "${kafka.topics.balancequery}", groupId = "helloworld")
	@SendTo("${kafka.topics.balanceoutcome}")
	public BalanceOutcome listen(@Headers MessageHeaders headers, @Payload BalanceQuery request) throws InterruptedException {
		logger.info("====> Message Headers = {}", headers);
		String customerId = request.getCustomerId();

		String storeName = kafkaConfig.getStateStores().getBalanceReadModel();
		StreamsMetadata metadata = kafkaStreams.metadataForKey(storeName, customerId, new StringSerializer());

		HostInfo hostInfo = metadata.hostInfo();
		logger.info("Reading local state store on {}", NetUtils.hostInfoToUrl(hostInfo));

		ReadOnlyKeyValueStore<String, BalanceState> store = kafkaStreams.store(storeName,
				QueryableStoreTypes.<String, BalanceState>keyValueStore());

		BalanceState balanceState = store.get(customerId);
		BigDecimal amount = balanceState != null ? balanceState.getAmount() : BigDecimal.ZERO;
		return new BalanceOutcome(clock.instant(), customerId, amount);
	}

}
