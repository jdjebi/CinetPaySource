package com.payout.notificationservice.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Component
public class TransactionListener {
	
	@Autowired
	TransactionListenerAdapter transactionFinalStatusAdapter;
	
	@KafkaListener(topics = "transactions_notification")
	public void listerner1(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws JsonMappingException, JsonProcessingException {
		
		transactionFinalStatusAdapter.on(message, partition);
		
	}

}
