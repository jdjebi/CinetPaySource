/**
 * @author Jean-Marc Dje Bi
 * @since 11-08-2021
 * @version 1.0.1
 */
package com.payout.backoffice.kafka;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.payout.backoffice.eventlog.entity.Event;

@Component("kafkaService")
public class KafkaService {
	
	@Autowired 
	private KafkaTemplate<String, Object> kafkaTemplate;
	
	@Autowired 
	private KafkaTemplate<String, Event> kafkaEventTemplate;
	
	private String eventTopic = "backoffice_events";
	
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
	
	public void pushEvent(String entity, String action, String ref, String level) {
		Event event = new Event(entity,action,ref,Event.EVENT_TYPE, level);
		sendEvent(event);
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
	public void pushEvent(String entity, String action, String ref, String level, HashMap<String, Object> data) {
		Event event = new Event(entity,action,ref,Event.INFO, level);
		event.getEventData().setMap(data);
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
	 * Emet un evenement de log auquel sans donnees associees
	 * @param entity
	 * @param action
	 * @param ref
	 * @param level
	 */
	public void pushLogEvent(String entity, String action, String ref, String level) {
		Event event = new Event(entity,action,ref,Event.LOG_TYPE, level);
		sendLogEvent(event);
	}
	
	/**
	 * Emet un evenement de log auquel est joint des donnes
	 * @param entity
	 * @param action
	 * @param ref
	 * @param level
	 * @param data
	 */
	public void pushLogEvent(String entity, String action, String ref, String level, HashMap<String, Object> data) {
		Event event = new Event(entity,action,ref,Event.LOG_TYPE, level);
		event.getEventData().setMap(data);
		sendLogEvent(event);
	}
}
