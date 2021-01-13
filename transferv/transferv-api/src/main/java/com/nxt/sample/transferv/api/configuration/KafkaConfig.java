package com.nxt.sample.transferv.api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.style.ToStringCreator;

@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {
	private String applicationId;
	private String bootstrapServersConfig;
	private String schemaRegistryUrlConfig;
	private Topics topics;

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public String getBootstrapServersConfig() {
		return bootstrapServersConfig;
	}

	public void setBootstrapServersConfig(String bootstrapServersConfig) {
		this.bootstrapServersConfig = bootstrapServersConfig;
	}

	public String getSchemaRegistryUrlConfig() {
		return schemaRegistryUrlConfig;
	}

	public void setSchemaRegistryUrlConfig(String schemaRegistryUrlConfig) {
		this.schemaRegistryUrlConfig = schemaRegistryUrlConfig;
	}

	public Topics getTopics() {
		return topics;
	}

	public void setTopics(Topics topics) {
		this.topics = topics;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("applicationId", applicationId)
				.append("bootstrapServersConfig", bootstrapServersConfig)
				.append("schemaRegistryUrlConfig", schemaRegistryUrlConfig).append("topics", topics).toString();
	}
}