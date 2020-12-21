package com.nxt.simplefund.controller.payload;

import java.math.BigDecimal;

import org.springframework.core.style.ToStringCreator;

public class BalancePayload {
	private BigDecimal amount;

	public BalancePayload() {
	}

	public BalancePayload(BigDecimal amount) {
		this.amount = amount;
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
