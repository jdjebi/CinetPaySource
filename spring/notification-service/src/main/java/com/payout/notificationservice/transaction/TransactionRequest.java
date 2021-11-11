package com.payout.notificationservice.transaction;


public class TransactionRequest {
	
	private TransactionHeader header;
	
	private TransactionBody body;
	
	public TransactionRequest(){
		 header = new TransactionHeader();
		 body = new TransactionBody();
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
