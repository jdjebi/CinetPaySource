package com.payout.eventlog.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.payout.eventlog.manager.SimpleEventAdapter;

@Component
public class TransactionListener {
	@Autowired 
	SimpleEventAdapter simpleEventAdapter;
	
	@KafkaListener(topics = "transfert_transactions_events", groupId = "transfert_transactions_events_group")
	public void listenTransfertTransactionEvents(String event) throws Exception {	
		// System.out.println("Listener 1");
		simpleEventAdapter.on(event);
	}
}
