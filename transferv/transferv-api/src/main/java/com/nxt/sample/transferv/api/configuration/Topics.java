package com.nxt.sample.transferv.api.configuration;

import org.springframework.core.style.ToStringCreator;

public class Topics {
	private String fundscommand;

	public String getFundscommand() {
		return fundscommand;
	}

	public void setFundscommand(String name) {
		this.fundscommand = name;
	}

	private String fundsoutcome;

	public String getFundsoutcome() {
		return this.fundsoutcome;
	}

	public void setFundsoutcome(String name) {
		this.fundsoutcome = name;
	}

	private String balancequery;

	public String getBalancequery() {
		return this.balancequery;
	}

	public void setBalancequery(String name) {
		this.balancequery = name;
	}

	private String balanceoutcome;

	public String getBalanceoutcome() {
		return this.balanceoutcome;
	}

	public void setBalanceoutcome(String name) {
		this.balanceoutcome = name;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("FundsCommand", fundscommand).append("Fundsoutcome", fundsoutcome)
				.append("BalanceQuery", balancequery).append("BalanceOutcome", balanceoutcome).toString();
	}
}
