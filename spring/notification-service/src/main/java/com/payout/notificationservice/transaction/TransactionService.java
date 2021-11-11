package com.payout.notificationservice.transaction;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.payout.notificationservice.kafka.KafkaService;

@Component
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;
	
	@Autowired
	KafkaService kafkaService;
	
	public void update(String trxRef, HashMap<String, Object> finalStatus) {
		
		Transaction trx = transactionRepository.findByTransactionId(trxRef).get();
		
		String status = (String) finalStatus.get("status");
		
		String comment = (String) finalStatus.get("comment");
		
		String operatorTransactionId = (String) finalStatus.get("operatorTransactionId");
				
		String resourceName = (String) finalStatus.get("resourceName");
		
		HashMap<String, Object> operatorDebugData = (HashMap<String, Object>) finalStatus.get("data");
		
		Date date = new Date();

		if(trx != null) {
						
			trx.setStatus(status);
			
			trx.setPauseComment(comment);
			
			trx.setFinishedAt(date);
			
			trx.setOperatorTransactionId(operatorTransactionId);
			
			trx.setResourceUsed(resourceName);
			
			trx.setOperatorDebugResponse(operatorDebugData);
			
			transactionRepository.save(trx);
		} 
		
	}
}
