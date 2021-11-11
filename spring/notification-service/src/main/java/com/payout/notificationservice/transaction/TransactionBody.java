package com.payout.notificationservice.transaction;

import java.util.HashMap;
import java.util.List;

public class TransactionBody {
	
	private Transaction transaction;
	
	private List<Transaction> transactions;
	
	private HashMap<String, Object> finalStatus;

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction newTrx) {
		this.transaction = newTrx;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public HashMap<String, Object> getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(HashMap<String, Object> finalStatus) {
		this.finalStatus = finalStatus;
	}
}
