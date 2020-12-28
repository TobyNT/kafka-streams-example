package com.nxt.simplefund.service;

import org.apache.kafka.streams.state.HostInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class HostInfoProvider implements ApplicationListener<ServletWebServerInitializedEvent> {

	private static final int DEFAULT_SERVER_PORT = 8080;

	private static final Logger logger = LoggerFactory.getLogger(HostInfoProvider.class);

	public HostInfoProvider(@Autowired Environment environment) {
		int port = getPortFromEnv(environment.getProperty("server.port"), DEFAULT_SERVER_PORT);
		currentHostInfo = new HostInfo("localhost", port);
	}

	private HostInfo currentHostInfo = null;

	@Override
	public void onApplicationEvent(ServletWebServerInitializedEvent event) {
		int port = event.getWebServer().getPort();
		logger.info("===> EventListener-Port={}", port);
		currentHostInfo = new HostInfo("localhost", port);
	}

	public HostInfo getHostInfo() {
		return currentHostInfo;
	}

	private int getPortFromEnv(String envPort, int defaultPort) {
		if (envPort == null) {
			logger.warn("No env server.port found");
			return defaultPort;
		}

		try {
			logger.info("Env server.port={}", envPort);
			return Integer.parseInt(envPort);
		} catch (NumberFormatException ex) {
			logger.error("Failed to parse server.port from environment. Use default port {}", defaultPort);
			return defaultPort;
		}
	}
}
