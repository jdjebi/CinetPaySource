package com.payout.psg.listener;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.model.TransactionMongo;
import com.payout.psg.repository.TransactionMongoRepository;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@Component
public class TransactionFinalStatusListener {
	
	@Autowired
	TransactionMongoRepository transactionMongoRepository;
	
	@Autowired
	TransactionFinalStatusAdapter transactionFinalStatusAdapter;
	
	@KafkaListener(topics = "transactions_final_status2")
	public void transactionProcessFailed(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) throws JsonMappingException, JsonProcessingException {
		transactionFinalStatusAdapter.on(message, partition);	
	}
	
	

}
