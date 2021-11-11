/**
 * @author Jean-Marc Dje Bi
 * @since 10-08-2021
 * @version 1
 */
package com.payout.dispatcher.kafka;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.payout.dispatcher.eventlog.entity.Event;
import com.payout.dispatcher.transactions.TransactionRequest;

/**
 * Classe gerant l'envoie des messages a kafka
 */
@Component("kafkaService")
public class KafkaService {
	
	
	@Autowired 
	private KafkaTemplate<String, Event> kafkaEventTemplate;
	
	@Autowired 
	private KafkaTemplate<String, TransactionRequest> kafkaTransactionRequestTemplate;
	
	/**
	 * Topic des logs
	 */	
	private String logsTopic = "backoffice_logs";
	
	
	/**
	 * Envoie une requete de transaction au service de transfert cible
	 * @param event instance d'une evenement
	 */
	public void sendTransactionRequestToTransfertService(TransactionRequest trxRequest, String transfertServiceKafkaTopic) throws JsonProcessingException {
		kafkaTransactionRequestTemplate.send(transfertServiceKafkaTopic,trxRequest); 
	}
	
	/**
	 * Emet un evenement sur le topic d'evenement reserve au dispatcher
	 * @param event instance d'une evenement
	 */
	public void sendEvent(Event event) {
		kafkaEventTemplate.send("dispatcher_transactions_events",event); 		
	}
	
	/**
	 * Emet un evenement de log sur le topic d'evenement
	 * @param event instance d'une evenement
	 */
	public void sendLogEvent(Event event) {
		kafkaEventTemplate.send(logsTopic,event); 		
	}
	
	/**
	 * Cree et envoie un evenement dans kafka
	 * @param entity Entite concerne
	 * @param action Titre de l'evenement
	 * @param ref Reference de l'entite
	 * @param strict type de d'evenement
	 * @param level pertinence de l'evenement
	 */
	public void pushEvent(String entity, String action, String ref, String strict, String level) {
		Event event = new Event(entity,action,ref,strict, level);
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
	
	/**
	 * Emet un evenement de log
	 * @param entity Entite concerne
	 * @param action Titre de l'evenement
	 * @param ref Reference de l'entite
	 * @param level Pertinence de l'evenement
	 */
	public void pushLogEvent(String entity, String action, String ref, String level) {
		Event event = new Event(entity,action,ref,Event.LOG_TYPE, level);
		sendLogEvent(event);
	}
	
	/**
	 * Emet un evenement d'echec
	 * @param trxRef
	 * @param data
	 */
	public void sendTransactionProcessFailed(String trxRef, HashMap<String, Object> data) {
		Event event = new Event("TRANSACTION","TransactionProcessFailed",trxRef,Event.EVENT_TYPE, Event.ERROR);
		event.getEventData().setMap(data);
		sendEvent(event);	
		kafkaEventTemplate.send("transactions_process_failed_eventlog",event); 	
		
	}
	
}
