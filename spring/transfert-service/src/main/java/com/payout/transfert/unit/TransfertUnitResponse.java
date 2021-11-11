package com.payout.transfert.unit;

import java.util.HashMap;

import com.payout.transfert.transactions.TransactionRequest;

public class TransfertUnitResponse {
	
	private TransactionRequest trxRequest;
	
	private HashMap<String, Object> response;
	
	private FinalStatus finalStatus;
	
	public TransfertUnitResponse() {
		
	}
	
	public TransfertUnitResponse(TransactionRequest trxRequest, HashMap<String, Object> response) {
		this.setTrxRequest(trxRequest);
		this.setResponse(response);
	}
	
	public TransfertUnitResponse(FinalStatus finalStatus) {
		this.trxRequest = new TransactionRequest();
		this.response = new HashMap<String, Object>();
		this.finalStatus = finalStatus;
	}

	public TransactionRequest getTrxRequest() {
		return trxRequest;
	}

	public void setTrxRequest(TransactionRequest trxRequest) {
		this.trxRequest = trxRequest;
	}

	public HashMap<String, Object> getResponse() {
		return response;
	}

	public void setResponse(HashMap<String, Object> response) {
		this.response = response;
	}

	public FinalStatus getFinalStatus() {
		return finalStatus;
	}

	public void setFinalStatus(FinalStatus finalStatus) {
		this.finalStatus = finalStatus;
	}

}
