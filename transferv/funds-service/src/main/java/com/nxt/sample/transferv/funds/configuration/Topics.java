package com.nxt.sample.transferv.funds.configuration;

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

	@Override
	public String toString() {
		return new ToStringCreator(this).append("FundsCommand", fundscommand).append("Fundsoutcome", fundsoutcome)
				.toString();
	}
}
