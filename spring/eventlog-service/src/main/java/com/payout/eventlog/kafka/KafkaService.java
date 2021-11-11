package com.payout.eventlog.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.payout.eventlog.dao.Event;


@Component("kafkaService")
public class KafkaService {
	
	@Autowired 
	private KafkaTemplate<String, Event> kafkaEventTemplate;
	
	public void send(Event event) {			
		kafkaEventTemplate.send("system_events",event);
	}
	
	public void redirectEventToGateway(Event event) {
		kafkaEventTemplate.send("gateway_transactions_events",event); 		
	}

	public void redirectTransactionProcessFailedEventToGateway(Event event) {			
		kafkaEventTemplate.send("transactions_process_failed_eventlog",event); 		
	}
	
	public void redirectTransactionFinalStatusToGateway(Event event) {			
		kafkaEventTemplate.send("transactions_final_status_gateway",event); 	
	}
}
