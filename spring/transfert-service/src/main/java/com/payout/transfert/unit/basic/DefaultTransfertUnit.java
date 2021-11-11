package com.payout.transfert.unit.basic;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import com.payout.transfert.adapter.TransactionProcessorAdapter;
import com.payout.transfert.core.kafka.KafkaService;
import com.payout.transfert.dao.Resource;
import com.payout.transfert.repository.ResourceRepository;
import com.payout.transfert.transactions.Transaction;
import com.payout.transfert.transactions.TransactionRequest;
import com.payout.transfert.unit.FinalStatus;
import com.payout.transfert.unit.TransfertUnitDispatcher;
import com.payout.transfert.unit.TransfertUnitResponse;

@Component
public class DefaultTransfertUnit {
	
	@Autowired
	KafkaService kafkaService;
	
	@Autowired
	ResourceRepository resourceRepository;
	
	@Autowired
	TransfertUnitDispatcher operatorTransfertUnitDispatcher;
	
    Logger logger = LoggerFactory.getLogger(TransactionProcessorAdapter.class);

	public TransfertUnitResponse makeTransfert(Transaction trx, Resource rx) {
				
		String transfertUrl = rx.getOperator_api_url();
		
		TransactionRequest trxRequest =  new TransactionRequest();
		
		RestTemplate restTemplate = new RestTemplate();
		
		HashMap<String, Object> response = restTemplate.postForObject(transfertUrl,null,HashMap.class);
				
		trxRequest.getBody().setTransaction(trx);
		
		trxRequest.getBody().setFinalStatus(response);
		
		TransfertUnitResponse transfertUnitResponse = new TransfertUnitResponse(trxRequest, response);
		
		FinalStatus finalStatus = new FinalStatus();
				
		String status = (String) response.get("status"); // SUCCESS OR FAILURE
		
		String comment = (String) response.get("comment");
		
		String operatorComment = "Transaction default status comment: " + comment;
		
		String operatorTransactionId = "OT." + System.currentTimeMillis();
		
		finalStatus.setBigdata(response);
		
		finalStatus.setStatus((String) response.get("status"));
		
		finalStatus.setComment(comment);
		
		finalStatus.setOperatorComment(operatorComment);
		
		finalStatus.setResourceStatus(true);
		
		finalStatus.setOperatorTransactionId(operatorTransactionId);
		
		finalStatus.setOperatorResource(operatorTransactionId);
		
		transfertUnitResponse.setFinalStatus(finalStatus);
		
		return transfertUnitResponse;
				
	}
}