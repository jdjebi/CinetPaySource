package com.payout.transfert.controller.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.payout.transfert.adapter.TransactionProcessorAdapter;
import com.payout.transfert.core.kafka.KafkaService;
import com.payout.transfert.repository.ResourceRepository;

@Component
public class ListernerController {
	
	@Autowired
	KafkaService kafkaService;
	
	@Autowired
	ResourceRepository resourceRepository;
	
	@Autowired
	TransactionProcessorAdapter transactionProcessorAdapter;
	
	@KafkaListener(topics ="trx_psp")
	public void getTransactionRequest(String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws JsonMappingException, JsonProcessingException, InterruptedException {
			
		transactionProcessorAdapter.on(message, partition);		
		
	}
	
}
