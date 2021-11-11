package com.payout.notificationservice.kafka;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payout.notificationservice.core.eventlog.entity.Event;
import com.payout.notificationservice.transaction.TransactionRequest;

@Component("kafkaService")
public class KafkaService {
	
	@Autowired 
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	
	@Autowired 
	private KafkaTemplate<String, Event> kafkaEventTemplate;
	
	private String eventTopic = "gateway_events";
		
	private String logsTopic = "backoffice_logs";

	
	/**
	 * Envoie un objet quelquonque sur le topic precise
	 * @param topic
	 * @param object
	 */
	public void sendMessage(String topic, Object object) {
		
		kafkaTemplate.send(topic,object); 		

	}
	
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
	
	public void pushLogEvent(String entity, String action, String ref, String level) {
		Event event = new Event(entity,action,ref,Event.LOG_TYPE, level);
		sendLogEvent(event);
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
	
	
	public void sendTransactionRequestToDispatcher(TransactionRequest trxRequest) throws JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		String trxRequestStr = null;
		
		trxRequestStr = mapper.writeValueAsString(trxRequest);
		kafkaTemplate.send("transaction_request_v2",trxRequestStr); 

	}
	
	public void sendTransactionProcessFailed(String trxRef, HashMap<String, Object> data) {
		Event event = new Event("TRANSACTION","TransactionProcessFailed",trxRef,Event.EVENT_TYPE, Event.ERROR);
		event.getEventData().setMap(data);
		sendEvent(event);	
	}
}
