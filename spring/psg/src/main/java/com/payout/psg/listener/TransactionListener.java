package com.payout.psg.listener;

import java.util.Date;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.model.TransactionMongo;
import com.payout.psg.repository.TransactionMongoRepository;
import com.payout.psg.transactions.Transaction;
import com.payout.psg.transactions.TransactionRepository;

@Component
public class TransactionListener {	
	
	@Autowired
	TransactionRepository transactionRepository;
		
	@KafkaListener(topics = "gateway_transactions_events", groupId = "gateway_transactions_events_group")
	public void simpleListener(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
		
		
		/*
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());

			Event event = mapper.readValue(message, Event.class);
			
			String trxRef = event.getEntityRef();
			
			TransactionMongo trx = transactionMongoRepository.findByRemoteId(trxRef);			
			
			if(event.getOriginService().equals("DISPATCHER")) {
				if(event.getAction().equals("TransactionReceived")) {	
					trx.setStatus("DISPATCHING");
				}else if(event.getAction().equals("TransactionSentToTransfertService")) {
					trx.setStatus("DISPATCHED");
				}
			}
			
			System.out.println(trxRef + ":: Partition: " + partition);
			
			trx.setFinishedAt(new Date());
			
			transactionMongoRepository.save(trx);
						
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		
	}
	
	@KafkaListener(topics = "transactions_process_failed_eventlog", groupId = "transactions_process_failed_eventlog_group")
	public void transactionProcessFailed(String message) throws JsonMappingException, JsonProcessingException {
		
			
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	
		Event event = mapper.readValue(message, Event.class);
		
		String trxRef = event.getEntityRef();
					
		Transaction trx = transactionRepository.findByTransactionId(trxRef).get();
		
		System.out.print(trx);
		
		if(trx != null) {
			
			HashMap<String, Object> map = event.getEventData().getMap();
			
			if(map != null) {
				trx.setPauseComment((String) map.get("error"));
			}
			
			trx.setStatus("PAUSE");
		
			trx.setFinishedAt(new Date());
	
			transactionRepository.save(trx);
			
		}	
		
	}


}
