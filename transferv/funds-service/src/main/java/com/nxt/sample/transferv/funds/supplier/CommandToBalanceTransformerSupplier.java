package com.nxt.sample.transferv.funds.supplier;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.streams.kstream.ValueTransformerWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerWithKeySupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nxt.sample.transferv.ActionStatus;
import com.nxt.sample.transferv.BalanceState;
import com.nxt.sample.transferv.BlockedAmount;
import com.nxt.sample.transferv.FundsCommand;
import com.nxt.sample.transferv.FundsOutcome;
import com.nxt.sample.transferv.funds.configuration.KafkaConfig;

public class CommandToBalanceTransformerSupplier
		implements ValueTransformerWithKeySupplier<String, FundsCommand, FundsOutcome> {
	private static final Logger logger = LoggerFactory.getLogger(CommandToBalanceTransformerSupplier.class);
	private KafkaConfig kafkaConfig;
	private Clock clock;

	public CommandToBalanceTransformerSupplier(KafkaConfig kafkaConfig, Clock clock) {
		this.kafkaConfig = kafkaConfig;
		this.clock = clock;
	}

	@Override
	public ValueTransformerWithKey<String, FundsCommand, FundsOutcome> get() {
		return new ValueTransformerWithKey<String, FundsCommand, FundsOutcome>() {

			private KeyValueStore<String, BalanceState> stateStore;

			@Override
			public void init(ProcessorContext context) {
				this.stateStore = (KeyValueStore<String, BalanceState>) context
						.getStateStore(kafkaConfig.getStateStores().getBalanceReadModel());
			}

			@Override
			public FundsOutcome transform(String readOnlyKey, FundsCommand value) {
				if (stateStore == null) {
					logger.error("State store {} not found", kafkaConfig.getStateStores().getBalanceReadModel());
					return null;
				}

				if (!readOnlyKey.equals(value.getCustomerId())) {
					logger.error("Transaction's customer ID mismatch: {} vs {}", readOnlyKey, value.getCustomerId());
				}

				FundsCommand command = value;

				BalanceState balanceState = getBalanceStateFromStore(readOnlyKey);
				FundsOutcome outcome = null;
				BalanceState needToUpdate = null;

				switch (command.getAction()) {
				case ADD:
					needToUpdate = new BalanceState(balanceState.getCustomerId(), balanceState.getAmount(),
							balanceState.getBlockedAmountsById());
					needToUpdate.setAmount(balanceState.getAmount().add(command.getAmount()));

					outcome = new FundsOutcome(command.getId(), command.getOccurredOn(), readOnlyKey,
							command.getAction(), ActionStatus.SUCCESS);
					break;
				case PREPARETOGIVE:
					BigDecimal rest = balanceState.getAmount().subtract(command.getAmount());
					if (rest.compareTo(BigDecimal.ZERO) < 0) {
						outcome = new FundsOutcome(command.getId(), command.getOccurredOn(), readOnlyKey,
								command.getAction(), ActionStatus.FAILURE);
					} else {
						Instant now = Instant.now(clock);
						BlockedAmount blockedAmount = new BlockedAmount(command.getId(), now,
								now.plus(3, ChronoUnit.MINUTES), command.getAmount());
						Map<String, BlockedAmount> blockedMap = new HashMap<>(balanceState.getBlockedAmountsById());
						blockedMap.put(command.getId(), blockedAmount);
						needToUpdate = new BalanceState(balanceState.getCustomerId(), rest, blockedMap);
						outcome = new FundsOutcome(command.getId(), command.getOccurredOn(), readOnlyKey,
								command.getAction(), ActionStatus.SUCCESS);
					}

					break;
				case PREPARETOTAKE:
					outcome = new FundsOutcome(value.getId(), value.getOccurredOn(), readOnlyKey, value.getAction(),
							ActionStatus.SUCCESS);

					break;
				}

				if (needToUpdate != null) {
					logger.info("Put to store = {}", needToUpdate);
					stateStore.put(readOnlyKey, needToUpdate);
				}
				return outcome;
			}

			@Override
			public void close() {
			}

			private BalanceState getBalanceStateFromStore(String customerId) {
				BalanceState balanceState = this.stateStore.get(customerId);

				if (balanceState == null) {
					return new BalanceState(customerId, BigDecimal.ZERO, new HashMap<String, BlockedAmount>());
				}

				return balanceState;
			}
		};
	}

}
