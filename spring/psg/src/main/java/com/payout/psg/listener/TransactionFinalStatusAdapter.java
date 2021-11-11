package com.payout.psg.listener;

import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
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
public class TransactionFinalStatusAdapter {
	
	@Autowired
	TransactionRepository transactionRepository;
	
	public void on(String message, int partition) throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		Event event = mapper.readValue(message, Event.class);
		
		String trxRef = event.getEntityRef();
				
		Transaction trx = transactionRepository.findByTransactionId(trxRef).get();
		
		System.out.print(trx);
				
		if(trx != null) {
			
			HashMap<String, Object> response = event.getEventData().getMap();
			
			trx.setStatus((String) response.get("status"));
			trx.setPauseComment((String) response.get("comment"));
			trx.setFinishedAt(new Date());
			System.out.println(trxRef + ":: Partition: " + partition);
			transactionRepository.save(trx);
		}
		
	}
}
