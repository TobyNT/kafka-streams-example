package com.nxt.simplefund.controller;

import com.nxt.simplefund.controller.payload.BalancePayload;

import feign.Param;
import feign.RequestLine;

public interface FundBalanceAdaptor {

	@RequestLine("GET /funds?customerId={customerId}")
	public BalancePayload getBalance(@Param("customerId") String customerId);
}
