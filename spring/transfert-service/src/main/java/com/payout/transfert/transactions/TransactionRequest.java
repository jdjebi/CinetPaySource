package com.payout.transfert.transactions;

public class TransactionRequest {
	
	private TransactionHeader header = new TransactionHeader();
	
	private TransactionBody body = new TransactionBody();
	
	public TransactionRequest(){
		
	}

	public TransactionHeader getHeader() {
		return header;
	}

	public void setHeader(TransactionHeader header) {
		this.header = header;
	}

	public TransactionBody getBody() {
		return body;
	}

	public void setBody(TransactionBody body) {
		this.body = body;
	}
	
}
