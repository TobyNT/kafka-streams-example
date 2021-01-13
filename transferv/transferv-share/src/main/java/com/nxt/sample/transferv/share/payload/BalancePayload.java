package com.nxt.sample.transferv.share.payload;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.ToStringBuilder;

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
		return new ToStringBuilder(this).append("amount", amount).toString();
	}
}
