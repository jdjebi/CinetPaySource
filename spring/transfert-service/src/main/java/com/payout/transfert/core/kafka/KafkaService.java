/**
 * @author Jean-marc Dje Bi
 * @version 1.1
 * @since 29-07-2021
 * @comment Doit etre mise a niveau
 */

package com.payout.transfert.core.kafka;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.payout.transfert.core.eventlog.entity.Event;
import com.payout.transfert.transactions.TransactionRequest;


@Component("kafkaService")
public class KafkaService {
	
	@Autowired 
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired 
	private KafkaTemplate<String, Event> kafkaEventTemplate;
	
	@Autowired 
	private KafkaTemplate<String, TransactionRequest> kafkaTransactionRequestTemplate;
	
	private String eventTopic = "transfert_transactions_events";
	
	private String logsTopic = "backoffice_logs";
	
	private String notificationTopic = "transactions_notification";
	
	public void sendEvent(Event event) {
				
		kafkaEventTemplate.send(eventTopic,event); 		

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
	
	/**
	 * Emet un evenement auquel est joint des donnees
	 * @param entity Entite concerne
	 * @param action Titre de l'evenement
	 * @param ref Reference de l'entite
	 * @param strict Type de d'evenement
	 * @param level Pertinence de l'evenement
	 * @param data Donnees attachees a l'evenement
	 */
	public void pushEvent(String entity, String action, String ref, String strict, String level, HashMap<String, Object> data) {
		Event event = new Event(entity,action,ref,strict, level);
		event.getEventData().setMap(data);
		sendEvent(event);
	}
	
	public void pushLogEvent(String entity, String action, String ref, String level) {
		Event event = new Event(entity,action,ref,Event.LOG_TYPE, level);
		sendLogEvent(event);
	}
	
	/* Transactions */
	
	public void sendTransactionEvent(String action, String trxRef, HashMap<String, Object> data) {
		Event event = new Event("TRANSACTION",action,trxRef,Event.EVENT_TYPE, Event.INFO);
		event.getEventData().setMap(data);
		sendEvent(event);	
	}
	
	public void sendTransactionEvent(String action, String trxRef){
		Event event = new Event("TRANSACTION",action,trxRef,Event.EVENT_TYPE, Event.INFO);
		sendEvent(event);	
	}
	
	/* Gateway */
	
	public void redirectTransactionProcessFailedEventToGateway(Event event) {			
		kafkaEventTemplate.send("transactions_process_failed_eventlog",event); 		
	}
	
	public void sendTransactionEventToGateway(String action, String trxRef, HashMap<String, Object> data) {	
		Event event = new Event("TRANSACTION",action,trxRef,Event.EVENT_TYPE, Event.INFO);
		event.getEventData().setMap(data);
		
		kafkaEventTemplate.send("transactions_final_status2",event); 
	
	}

	public ListenableFuture<SendResult<String, TransactionRequest>> sendTransactionRequestToNotification(TransactionRequest trxRequest) {
		return kafkaTransactionRequestTemplate.send(notificationTopic,trxRequest); 		
	}
	
	public void sendTransactionProcessFailed(String trxRef, HashMap<String, Object> data) {
		Event event = new Event("TRANSACTION","TransactionProcessFailed",trxRef,Event.EVENT_TYPE, Event.ERROR);
		event.getEventData().setMap(data);
		kafkaEventTemplate.send("transactions_process_failed_eventlog",event); 	

	}
	
}
