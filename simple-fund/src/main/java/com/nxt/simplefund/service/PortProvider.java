package com.nxt.simplefund.service;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PortProvider implements ApplicationListener<WebServerInitializedEvent> {
	private int localWebServerPort;

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		localWebServerPort = event.getWebServer().getPort();
	}

	public int getPort() {
		return localWebServerPort;
	}
}
