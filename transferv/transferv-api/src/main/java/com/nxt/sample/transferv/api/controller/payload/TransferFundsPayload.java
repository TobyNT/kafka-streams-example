package com.nxt.sample.transferv.api.controller.payload;

import java.math.BigDecimal;

import org.springframework.core.style.ToStringCreator;

public class TransferFundsPayload {
	private String senderId;
	private String receiverId;
	private BigDecimal amount;

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("senderId", senderId).append("receiverId", receiverId)
				.append("amount", amount).toString();
	}
}
