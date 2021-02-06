package com.nxt.sample.transferv.funds.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import com.nxt.sample.transferv.BalanceOutcome;
import com.nxt.sample.transferv.BalanceQuery;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;

@Configuration
public class KafkaRequestReplyConfiguration {
	private static final Logger logger = LoggerFactory.getLogger(KafkaRequestReplyConfiguration.class);

	private KafkaConfig kafkaConfig;

	@Autowired
	public KafkaRequestReplyConfiguration(KafkaConfig kafkaConfig) {
		this.kafkaConfig = kafkaConfig;
	}

	@Bean
	public Map<String, Object> consumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServersConfig());
		props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "helloworld");
		props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
		return props;
	}

	@Bean
	public ConsumerFactory<String, BalanceQuery> consumerFactory() {
		return new DefaultKafkaConsumerFactory(consumerConfigs());
	}

	/**
	 * Must name this method kafkaListenerContainerFactory when
	 * use @KafkaListener as it will lookup the bean having that name or create
	 * the default one.
	 * 
	 * @return
	 */
	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, BalanceQuery> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, BalanceQuery> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		factory.setReplyTemplate(kafkaTemplate());

		return factory;
	}

	@Bean
	public KafkaTemplate<String, BalanceOutcome> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	@Bean
	public Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServersConfig());
		props.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfig.getSchemaRegistryUrlConfig());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		props.put("value.subject.name.strategy", TopicRecordNameStrategy.class);

		return props;
	}

	public ProducerFactory<String, BalanceOutcome> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

}
