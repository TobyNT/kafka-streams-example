package com.nxt.sample.transferv.api.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import com.nxt.sample.transferv.BalanceOutcome;
import com.nxt.sample.transferv.BalanceQuery;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;

@Configuration
public class KafkaBalanceRequestReplyConfiguration {

	private KafkaConfig kafkaConfig;

	@Autowired
	public KafkaBalanceRequestReplyConfiguration(KafkaConfig kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}

	@Bean("replyKafkaTemplate")
	public ReplyingKafkaTemplate<String, BalanceQuery, BalanceOutcome> replyKafkaTemplate(
			ProducerFactory producerFactory, KafkaMessageListenerContainer<String, BalanceOutcome> container) {
		return new ReplyingKafkaTemplate<>(producerFactory, container);
	}

	@Bean
	public KafkaMessageListenerContainer<String, BalanceOutcome> replyContainer(ConsumerFactory consumerFactory) {
		ContainerProperties containerProperties = new ContainerProperties(kafkaConfig.getTopics().getBalanceoutcome());
		return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
	}

	@Bean
	public ConsumerFactory<String, SpecificRecord> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServersConfig());
		props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "helloworld");
		props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);

		return new DefaultKafkaConsumerFactory<String, SpecificRecord>(props);
	}

}
