package com.nxt.sample.transferv.api.configuration;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;

@Configuration
public class GeneralConfigurations {

	private KafkaConfig kafkaConfig;

	@Autowired
	public GeneralConfigurations(KafkaConfig kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}

	@Bean
	public Clock createClock() {
		return Clock.systemUTC();
	}

	@Bean
	public ProducerFactory<String, SpecificRecord> producerFactory() {
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServersConfig());
		props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put("value.subject.name.strategy", TopicRecordNameStrategy.class);

		return new DefaultKafkaProducerFactory<String, SpecificRecord>(props);
	}
}
