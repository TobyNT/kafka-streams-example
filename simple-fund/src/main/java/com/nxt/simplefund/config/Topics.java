package com.nxt.simplefund.config;

import org.springframework.core.style.ToStringCreator;

public class Topics {
	private String balance;

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("balance", balance).toString();
	}
}
