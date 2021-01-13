package com.nxt.sample.transferv.funds.supplier;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nxt.sample.transferv.BalanceState;
import com.nxt.sample.transferv.BlockedAmount;
import com.nxt.sample.transferv.FundsCommand;
import com.nxt.sample.transferv.funds.configuration.KafkaConfig;

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
		if (record instanceof FundsCommand) {
			FundsCommand commandRecord = (FundsCommand) record;
			String customerId = commandRecord.getCustomerId();
			logger.info("Customer ID = {}", customerId);

			BigDecimal currentAmount = getBalanceFromStateStore(customerId);

			BigDecimal newBalance = currentAmount.add(commandRecord.getAmount());
			stateStore.put(commandRecord.getCustomerId(),
					new BalanceState(customerId, newBalance, new HashMap<String, BlockedAmount>()));

			logger.info("Processed {}\n\trecord: {}", commandRecord.getSchema().getName(), commandRecord);
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
