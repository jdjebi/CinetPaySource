package com.payout.psg.transactions;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public class TransactionReceiveThread extends Thread {
	
	private List<TransactionReceiveStatus> trxReceiveStatusList;
	
	private TransactionProcessComponentsProxy trxComponentsProxy;
		
	private Transaction trx;

	public TransactionReceiveThread(Transaction trx, TransactionProcessComponentsProxy trxComponentsProxy, List<TransactionReceiveStatus> trxReceiveStatusList){
	    super("Thread-" + trx.getTransactionId());
	    this.trxReceiveStatusList = trxReceiveStatusList;
	    this.trx = trx;
	    this.trxComponentsProxy = trxComponentsProxy;
	}
	
	public TransactionReceiveThread(ThreadGroup threadParent, Transaction trx, TransactionProcessComponentsProxy trxComponentsProxy, List<TransactionReceiveStatus> trxReceiveStatusList){
	    super(threadParent, "Thread-" + trx.getTransactionId());
	    this.trxReceiveStatusList = trxReceiveStatusList;
	    this.trxComponentsProxy = trxComponentsProxy;
	    this.trx = trx;
	}
	
	public void run(){
		
		TransactionReceiver transactionReceiver = new TransactionReceiver(trxComponentsProxy);

		TransactionReceiveStatus status = null;
		
		try {
			
			status = transactionReceiver.receive(trx);
			
		} catch (JsonProcessingException e) {
			
			status = new TransactionReceiveStatus(trx.getTransactionId());
			
			status.setRefuse();
			
			status.setComment("SERVER_ERROR");
			
			e.printStackTrace();
		}
		
		this.trxReceiveStatusList.add(status);
		
	}

	public List<TransactionReceiveStatus> getTrxReceiveStatusList() {
		return trxReceiveStatusList;
	}
	
}
