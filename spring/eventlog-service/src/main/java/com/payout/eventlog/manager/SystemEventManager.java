package com.payout.eventlog.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.payout.eventlog.dao.Event;
import com.payout.eventlog.kafka.KafkaService;
import com.payout.eventlog.repository.EventRepository;

@Component
public class SystemEventManager {
	
	@Autowired EventRepository eventRepository;
	
	@Autowired KafkaService kafkaService;
	
	public void manage(Event event) throws Exception {
		
		eventRepository.save(event);
		
		/*
		if(event.getEntityType().equals("TRANSACTION")) {
			manageTransaction(event);
		}
		
		*/
	}
	
	public void manageTransaction(Event event) throws Exception {
		
		String eventOriginService = event.getOriginService();
		
		if(!event.getAction().equals("TransactionProcessFailed")) {
			switch(eventOriginService) {
				case "GATEWAY":		
					break;
				
				case "TRANSFERT-SERVICE":		
					transactionEventAnalyzer(event);
					break;
			}
		}else {
			transactionProcessFailedAnalyzer(event);
		}
		
	}

	public void dispatcherEventAnalyzer(Event event) {
		
		String action = event.getAction();
				
		if(action.equals("TransactionReceived")) {
			// kafkaService.redirectEventToGateway(event);		
		}else if(action.equals("TransactionSentToTransfertService")) {
			kafkaService.redirectEventToGateway(event);		
		}
		
	}
	
	public void transactionProcessFailedAnalyzer(Event event) {
		// kafkaService.redirectTransactionProcessFailedEventToGateway(event);	
	}
	
	public void transactionEventAnalyzer(Event event) {
		
		String action = event.getAction();
				
		if(action.equals("TransactionFinalStatusReceived")) {
			// kafkaService.redirectTransactionFinalStatusToGateway(event);		
		}
		
	}
}
