package com.nxt.simplefund.supplier;

import java.math.BigDecimal;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nxt.simplefund.BalanceState;
import com.nxt.simplefund.FundsAdded;
import com.nxt.simplefund.config.KafkaConfig;

public class BalanceProcessor implements Processor<String, SpecificRecord> {
	private Logger logger = LoggerFactory.getLogger(BalanceProcessor.class);
	private KeyValueStore<String, BalanceState> stateStore;

	private KafkaConfig kafkaConfig;

	@Autowired
	public BalanceProcessor(KafkaConfig kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}

	@Override
	public void init(ProcessorContext context) {
		logger.info("Init balance processor, config={}", kafkaConfig);
		stateStore = (KeyValueStore<String, BalanceState>) context
				.getStateStore(kafkaConfig.getStateStores().getBalanceReadModel());
		logger.info("StateStore={}", stateStore);
	}

	@Override
	public void process(String key, SpecificRecord record) {
		if (record instanceof FundsAdded) {
			FundsAdded faRecord = (FundsAdded) record;
			String customerId = faRecord.getCustomerId();
			logger.info("Customer ID = {}", customerId);

			BigDecimal currentAmount = getBalanceFromStateStore(customerId);

			BigDecimal newBalance = currentAmount.add(faRecord.getAmount());
			stateStore.put(faRecord.getCustomerId(), new BalanceState(customerId, newBalance));

			logger.info("Processed {}\n\trecord: {}", faRecord.getSchema().getName(), faRecord);
		}
	}

	private BigDecimal getBalanceFromStateStore(String customerId) {
		BalanceState balanceState = stateStore.get(customerId);
		BigDecimal currentAmount;
		if (balanceState == null) {
			currentAmount = BigDecimal.ZERO;
		} else {
			currentAmount = balanceState.getAmount();
		}

		return currentAmount != null ? currentAmount : BigDecimal.ZERO;
	}

	@Override
	public void close() {
	}
}
