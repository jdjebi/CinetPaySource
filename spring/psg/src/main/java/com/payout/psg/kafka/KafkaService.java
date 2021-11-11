package com.payout.psg.kafka;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.psg.eventlog.entity.Event;
import com.payout.psg.transactions.TransactionRequest;

@Component("kafkaService")
public class KafkaService {
	
	@Autowired 
	protected KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired 
	protected KafkaTemplate<String, Event> kafkaEventTemplate;
	
	protected String eventTopic = "gateway_events";
		
	protected String logsTopic = "backoffice_logs";

	public void sendEvent(Event event) {
				
		ListenableFuture<SendResult<String, Event>> future = kafkaEventTemplate.send(eventTopic,event); 
			
		future.addCallback(new ListenableFutureCallback<SendResult<String,Event>>(){
			
		    @Override
		    public void onSuccess(SendResult<String, Event> result) {
		    	// System.out.println("Success :" + event.getEntityRef());
		    }

		    @Override
		    public void onFailure(Throwable ex) {		    	
		    	// System.out.println("Fail :" + event.getEntityRef());		
		    }
		    
		});
				
	}
	
	public void sendLogEvent(Event event) {
		
		kafkaEventTemplate.send(logsTopic,event); 		

	}
	
	public void pushEvent(String entity, String action, String ref, String strict, String level) {
		Event event = new Event(entity,action,ref,strict, level);
		sendEvent(event);
	}
	
	public void pushEvent(String entity, String action, String ref, String level) {
		Event event = new Event(entity,action,ref,Event.EVENT_TYPE, level);
		sendEvent(event);
	}
	
	public void pushLogEvent(String entity, String action, String ref, String level) {
		Event event = new Event(entity,action,ref,Event.LOG_TYPE, level);
		sendLogEvent(event);
	}
	
	public ListenableFuture<SendResult<String, String>> sendTransactionRequestToDispatcher(TransactionRequest trxRequest) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		String trxRequestStr = null;
		
		trxRequestStr = mapper.writeValueAsString(trxRequest);
		
		return kafkaTemplate.send("transaction_request_v2",trxRequestStr);
		
	}
	
	public void sendTransactionProcessFailed(String trxRef, HashMap<String, Object> data) {
		Event event = new Event("TRANSACTION","TransactionProcessFailed",trxRef,Event.EVENT_TYPE, Event.ERROR);
		event.getEventData().setMap(data);
		sendEvent(event);	
	}
}
