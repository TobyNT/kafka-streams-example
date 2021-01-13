package com.nxt.sample.transferv.funds.configuration;

import org.springframework.core.style.ToStringCreator;

public class StateStores {
	private String balanceReadModel;

	public String getBalanceReadModel() {
		return balanceReadModel;
	}

	public void setBalanceReadModel(String balanceReadModel) {
		this.balanceReadModel = balanceReadModel;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("balanceReadModel", balanceReadModel).toString();
	}
}