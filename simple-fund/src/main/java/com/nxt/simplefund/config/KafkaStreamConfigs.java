package com.nxt.simplefund.config;

import java.time.Clock;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import com.nxt.simplefund.BalanceState;
import com.nxt.simplefund.service.PortProvider;
import com.nxt.simplefund.supplier.BalanceProcessor;
import com.nxt.simplefund.utility.NetUtils;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

@Configuration
public class KafkaStreamConfigs {

	private Logger logger = LoggerFactory.getLogger(KafkaStreamConfigs.class);

	private KafkaConfig kafkaConfig;

	public KafkaStreamConfigs(KafkaConfig kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}

	@Bean
	public KafkaStreams createKafkaStreams(@Autowired HostInfo hostInfo) {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, kafkaConfig.getApplicationId());
		props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, NetUtils.hostInfoToUrl(hostInfo));
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServersConfig());
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
		props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		props.put("value.subject.name.strategy", TopicRecordNameStrategy.class);

		StreamsBuilder builder = new StreamsBuilder();

		Serde<BalanceState> valueSerde = new SpecificAvroSerde<BalanceState>();
		Map<String, String> serdeConfig = Collections.singletonMap(
				AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		valueSerde.configure(serdeConfig, false);

		ProcessorSupplier supplier = () -> new BalanceProcessor(kafkaConfig);

		builder.addStateStore(Stores.keyValueStoreBuilder(
				Stores.persistentKeyValueStore(kafkaConfig.getStateStores().getBalanceReadModel()), Serdes.String(),
				valueSerde)).stream(kafkaConfig.getTopics().getBalance())
				.process(supplier, kafkaConfig.getStateStores().getBalanceReadModel());

		return new KafkaStreams(builder.build(), props);
	}

	@Bean
	public KafkaProducer<String, SpecificRecord> createKafkaProducer() {
		Properties props = new Properties();
		props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServersConfig());
		props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put("value.subject.name.strategy", TopicRecordNameStrategy.class);

		return new KafkaProducer(props);
	}

	@Bean
	@Lazy
	public HostInfo createHostInfo(@Autowired ServletWebServerApplicationContext server) {
		int localWebServerPort = server.getWebServer().getPort();
		logger.info("===> Server port = {}", localWebServerPort);
		return new HostInfo("localhost", localWebServerPort);
	}

	@Bean
	public Clock getClock() {
		return Clock.systemUTC();
	}
}
