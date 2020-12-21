package com.nxt.simplefund.controller.payload;

import java.math.BigDecimal;

import org.springframework.core.style.ToStringCreator;

public class AddFundsPayload {
	private String customerId;
	private BigDecimal amount;

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).toString();
	}
}
