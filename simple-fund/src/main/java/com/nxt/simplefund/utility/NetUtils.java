package com.nxt.simplefund.utility;

import org.apache.kafka.streams.state.HostInfo;

public class NetUtils {

	public static String hostInfoToUrl(HostInfo hostInfo) {
		return String.format("%s:%s", hostInfo.host(), hostInfo.port());
	}
}
