package com.nxt.sample.transferv.funds.configuration;

import java.time.Clock;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nxt.sample.transferv.BalanceState;
import com.nxt.sample.transferv.FundsCommand;
import com.nxt.sample.transferv.funds.service.HostInfoProvider;
import com.nxt.sample.transferv.funds.supplier.CommandToBalanceTransformerSupplier;
import com.nxt.sample.transferv.funds.utility.NetUtils;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

@Configuration
public class KafkaStreamConfigs {

	private static final Logger logger = LoggerFactory.getLogger(KafkaStreamConfigs.class);

	private KafkaConfig kafkaConfig;

	@Autowired
	public KafkaStreamConfigs(KafkaConfig kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}

	@Bean
	public KafkaStreams createKafkaStreams(@Autowired HostInfoProvider hostInfoProvider) {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfig.getApplicationId());
		props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, NetUtils.hostInfoToUrl(hostInfoProvider.getHostInfo()));
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServersConfig());
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
		props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
		props.put("value.subject.name.strategy", TopicRecordNameStrategy.class);

		StreamsBuilder builder = new StreamsBuilder();

		Serde<BalanceState> valueSerde = new SpecificAvroSerde<BalanceState>();
		Map<String, String> serdeConfig = Collections.singletonMap(
				AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		valueSerde.configure(serdeConfig, false);

		StoreBuilder<KeyValueStore<String, BalanceState>> keyValueStoreBuilder = Stores.keyValueStoreBuilder(
				Stores.persistentKeyValueStore(kafkaConfig.getStateStores().getBalanceReadModel()), Serdes.String(),
				valueSerde);
		KStream<String, FundsCommand> fundsCommandStream = builder.addStateStore(keyValueStoreBuilder)
				.stream(kafkaConfig.getTopics().getFundscommand());
		fundsCommandStream
				.transformValues(new CommandToBalanceTransformerSupplier(kafkaConfig, getClock()),
						kafkaConfig.getStateStores().getBalanceReadModel())
				.to(kafkaConfig.getTopics().getFundsoutcome());

		return new KafkaStreams(builder.build(), props);
	}

	@Bean
	public Clock getClock() {
		return Clock.systemUTC();
	}
}
